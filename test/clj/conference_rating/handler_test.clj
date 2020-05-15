(ns conference-rating.handler-test
  (:use [conference-rating.handler])
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [ring.mock.request :refer [request body header]]
            [clojure.data.json :as json]
            [monger.collection :as mc]
            [schema.test]
            [conference-rating.testdata :refer [some-rating-with some-rating some-conference-with]]
            [clojure.string :as s]
            [java-time :as jtime])
  (:import (com.github.fakemongo Fongo)
           (org.bson.types ObjectId)))

(use-fixtures :once schema.test/validate-schemas)

(def some-api-key "some-api-key")
(defn json-body [response]
  (json/read-str (:body response) :key-fn keyword))

(defn get-content-type [response]
  (get (:headers response) "Content-Type"))

(defn create-app-and-call [db request]
  ((app db true some-api-key) request))

(defn json-body-for [db request]
  (json-body (create-app-and-call db request)))

(defn- create-mock-db []
  (let [fongo (Fongo. "some mock mongodb")]
    (.getDB fongo "crdb")))

(defn with-okta-data [request]
  (-> request
      (assoc-in [:session :okta/user] "some@user.com")
      (assoc-in [:session :okta/attributes] {"firstName" ["SomeFirstName"]
                                             "lastName"  ["SomeLastName"]})))
(defn- create-conference-test [db conference]
  (create-app-and-call db (-> (request :post "/api/conferences/")
                              (body (json/write-str conference))
                              (header :content-type "application/json"))))

(deftest acceptance-test
  (testing "should have an index-page"
    (is (= 200 (:status ((app (create-mock-db) true some-api-key) (request :get "/"))))))
  (testing "should return all ratings of a conference as json"
    (let [db (create-mock-db)
          response (create-app-and-call db
                                        (-> (request :post "/api/conferences/someConferenceId/ratings")
                         (with-okta-data)
                         (body (json/write-str
                                 (some-rating-with :comment {:comment "some comment with a <p>p tag</p>"}
                                                   :rating {:overall    5
                                                            :talks      1
                                                            :venue      2
                                                            :networking 3})))
                         (header :content-type "application/json")))]
      (is (= 201 (:status response)))
      (let [ratings-response (create-app-and-call db (request :get "/api/conferences/someConferenceId/ratings"))
            rating-list (json/read-str (:body ratings-response) :key-fn keyword) ]
        (is (= 200 (:status ratings-response) ))
        (is (= "application/json; charset=UTF-8" (get-content-type ratings-response)))
        (is (= 1 (count rating-list)))
        (is (= "some@user.com" (:email (:user (first rating-list)))))
        (is (= "SomeFirstName" (:firstName (:user (first rating-list)))))
        (is (= "SomeLastName" (:lastName (:user (first rating-list)))))
        (is (= "some comment with a &lt;p&gt;p tag&lt;/p&gt;" (:comment (:comment (first rating-list)))))
        (is (= 5 (:overall (:rating (first rating-list))))))))
  (testing "rate limiting"
    (let [db (create-mock-db)
          app-instance (app db true some-api-key)
          responses (doall (repeatedly 101 (fn []
                                             (app-instance (-> (request :post "/api/conferences/")
                                                               (body (json/write-str (some-conference-with {:name "some name" :description "some description with a <p>p tag</p>"})))
                                                               (header :content-type "application/json"))))))]
      (is (= 429 (:status (last responses))))))
  (testing "should add, edit and delete a conference"
    (let [db (create-mock-db)]
      (let [response (create-app-and-call db (-> (request :post "/api/conferences/")
                                                 (body (json/write-str (some-conference-with {:name "some name" :description "some description with a <p>p tag</p>"})))
                                                 (header :content-type "application/json")))]
        (is (= 201 (:status response)))
        (is (.startsWith (get-in response [:headers "Location"]) "/api/conferences/"))
        (let [raw-response (create-app-and-call db (request :get (get-in response [:headers "Location"])))]
          (is (= "application/json; charset=UTF-8" (get-content-type raw-response)))
          (let [conference-response (json-body raw-response)]
            (is (= "some description with a &lt;p&gt;p tag&lt;/p&gt;" (:description conference-response)))
            (is (= "some name" (:name conference-response)))
            (is (map? (:aggregated-ratings conference-response)))))
        (let [raw-response (create-app-and-call db (request :get "/api/conferences"))]
          (is (= "application/json; charset=UTF-8" (get-content-type raw-response)))
          (let [conferences (json-body raw-response)]
            (is (= 1 (count conferences)))
            (is (= "some description with a &lt;p&gt;p tag&lt;/p&gt;" (:description (first conferences))))
            (is (= "some name" (:name (first conferences))))
            (is (map? (:aggregated-ratings (first conferences))))

            (let [id (:_id (first conferences))
                  response (create-app-and-call db (-> (request :put (str "/api/conferences/" id "/edit"))
                                                       (body (json/write-str (some-conference-with {
                                                                                           :name "some other name"
                                                                                           :description "some other description with <tag> and &"})))
                                                       (header :content-type "application/json")))]
              (is (= 200 (:status response)))
              (is (= "application/json; charset=UTF-8" (get-content-type response)))
              (is (= id (:_id (json-body response))))
              (let [raw-response (create-app-and-call db (request :get (str "/api/conferences/" id)))]
                (is (= "application/json; charset=UTF-8" (get-content-type raw-response)))
                (let [conference (json-body raw-response)]
                  (is (= "some other name" (:name conference)))
                  (is (= "some other description with &lt;tag&gt; and &amp;" (:description conference))))))
            (let [id (:_id (first conferences))
                  response (create-app-and-call db (request :delete (str "/api/conferences/" id)))]
              (is (= 204 (:status response))))))
        (let [conferences (json-body-for db (request :get "/api/conferences"))]
          (is (= 0 (count conferences)))))))

  (testing "conference validation"
    (testing "series too long"
      (let [db (create-mock-db)]
        (let [response (create-app-and-call db (-> (request :post "/api/conferences/")
                                                   (body (json/write-str (some-conference-with {:series (s/join (repeat 1000 "x"))})))
                                                   (header :content-type "application/json")))]
          (is (= 500 (:status response))))))
    (testing "name too long"
      (let [db (create-mock-db)]
        (let [response (create-app-and-call db (-> (request :post "/api/conferences/")
                                                   (body (json/write-str (some-conference-with {:name (s/join (repeat 1000 "x"))})))
                                                   (header :content-type "application/json")))]
          (is (= 500 (:status response))))))
    (testing "link too long"
      (let [db (create-mock-db)]
        (let [response (create-app-and-call db (-> (request :post "/api/conferences/")
                                                   (body (json/write-str (some-conference-with {:link (s/join (repeat 2000 "x"))})))
                                                   (header :content-type "application/json")))]
          (is (= 500 (:status response))))))
    (testing "description too long"
      (let [db (create-mock-db)]
        (let [response (create-app-and-call db (-> (request :post "/api/conferences/")
                                                   (body (json/write-str (some-conference-with {:description (s/join (repeat 20000 "x"))})))
                                                   (header :content-type "application/json")))]
          (is (= 500 (:status response)))))))
  (testing "rating validation"
    (testing "name too long"
      (let [db (create-mock-db)]
        (let [response (create-app-and-call db (-> (request :post "/api/conferences/someConferenceId/ratings")
                                                   (body (json/write-str (some-rating-with :comment {:name (s/join (repeat 1000 "x")) :comment "some comment"})))
                                                   (header :content-type "application/json")))]
          (is (= 500 (:status response))))))
    (testing "description too long"
      (let [db (create-mock-db)]
        (let [response (create-app-and-call db (-> (request :post "/api/conferences/someConferenceId/ratings")
                                                   (body (json/write-str (some-rating-with :comment {:name "some name" :comment (s/join (repeat 20000 "x"))})))
                                                   (header :content-type "application/json")))]
          (is (= 500 (:status response)))))))
  (testing "series suggestions"
    (let [db (create-mock-db)]
      (create-app-and-call db (-> (request :post "/api/conferences/")
                                  (body (json/write-str (some-conference-with {:name "some name" :description "some description" :series "some series"})))
                                  (header :content-type "application/json")))
      (create-app-and-call db (-> (request :post "/api/conferences/")
                                  (body (json/write-str (some-conference-with {:name "some other name" :description "some other description" :series "some series"})))
                                  (header :content-type "application/json")))
      (create-app-and-call db (-> (request :post "/api/conferences/")
                                  (body (json/write-str (some-conference-with {:name "some other name" :description "some other description" :series "other series"})))
                                  (header :content-type "application/json")))

      (testing "should find suggestions for existing series"
        (let [raw-response (create-app-and-call db (request :get "/api/series/suggestions?q=some"))]
          (is (= 200 (:status raw-response)))
          (is (= "application/json; charset=UTF-8" (get-content-type raw-response)))
          (is (= ["some series"] (json-body raw-response)))))))
  (testing "should fail if incomplete data is written to ratings"
    (let [db (create-mock-db)
          response (create-app-and-call db (-> (request :post "/api/conferences/someConferenceId/ratings")
                                               (body (json/write-str {:some "random value"}))
                                               (header :content-type "application/json")))]
      (is (= 500 (:status response)))))
  (testing "identity endpoint should return correct contact type"
    (let [db (create-mock-db)]
      (let [response (create-app-and-call db (request :get (str "/api/user/identity")))]
        (is (= 200 (:status response)))
        (is (= "application/json; charset=UTF-8" (get-content-type response))))))

  (testing "acceptance test get conferences"
      (let [db (create-mock-db) today (jtime/local-date-time) one-day (jtime/days 1) five-days (jtime/days 5)]
        (create-conference-test db (some-conference-with
                             {:name "past-conf"
                              :from (jtime/format :iso-date-time (jtime/minus today five-days))
                              :to (jtime/format :iso-date-time (jtime/minus today one-day))
                              }))
        (create-conference-test db (some-conference-with
                                     {:name "future-conf"
                                      :from (jtime/format :iso-date-time (jtime/plus today one-day))
                                      :to (jtime/format :iso-date-time (jtime/plus today five-days))
                                      }))
        (testing "get past conferences" (let [raw-response (create-app-and-call db (request :get "/api/conferences/past?current-page=1&per-page=10"))]
          (let [conferences (json-body raw-response)]
            (is (= 1 (count conferences)))
            (is (= "past-conf" (:name (first conferences)))))))
        (testing "get future conferences" (let [raw-response (create-app-and-call db (request :get "/api/conferences/future?current-page=1&per-page=10"))]
          (let [conferences (json-body raw-response)]
            (is (= 1 (count conferences)))
            (is (= "future-conf" (:name (first conferences)))))))
        ))
  )

(deftest backwards-compatibility-test
  (testing "that we get valid ratings even if there is crap in the db"
    (let [db (create-mock-db)
          conference-id (ObjectId.)
          rating-id (ObjectId.)]
      (mc/insert db "conferences" {:_id conference-id :foo :bar})
      (mc/insert db "ratings" {:_id rating-id :conference-id conference-id :foo :bar})
      (let [response (create-app-and-call db (request :get (str "/api/conferences/" conference-id)))]
        (is (= 200 (:status response)))))))

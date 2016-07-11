(ns clj.conference-rating.handler-test
  (:use [conference-rating.handler])
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [ring.mock.request :refer [request body header]]
            [clojure.data.json :as json]
            [monger.collection :as mc]
            [schema.test]
            [conference-rating.testdata :refer [some-rating-with some-rating some-conference-with]]
            [clojure.string :as s])
  (:import (com.github.fakemongo Fongo)
           (org.bson.types ObjectId)))

(use-fixtures :once schema.test/validate-schemas)

(defn json-body [response]
  (json/read-str (:body response) :key-fn keyword))

(defn json-body-for [db request]
  (json-body ((app db true) request)))

(defn- create-mock-db []
  (let [fongo (Fongo. "some mock mongodb")]
    (.getDB fongo "crdb")))

(deftest acceptance-test
  (testing "should have an index-page"
    (is (= 200 (:status ((app (create-mock-db) true) (request :get "/"))))))
  (testing "should return all ratings of a conference as json"
    (let [db (create-mock-db)
          response ((app db true) (-> (request :post "/api/conferences/someConferenceId/ratings")
                                 (body (json/write-str
                                         (some-rating-with :comment {:name "Bob" :comment "some comment with a <p>p tag</p>"}
                                                           :rating {:overall 5
                                                                    :talks 1
                                                                    :venue 2
                                                                    :networking 3})))
                                 (header :content-type "application/json")))]
      (is (= 201 (:status response)))
      (let [ratings-response ((app db true) (request :get "/api/conferences/someConferenceId/ratings"))
            rating-list (json/read-str (:body ratings-response) :key-fn keyword) ]
        (is (= 200 (:status ratings-response) ))
        (is (= 1 (count rating-list)))
        (is (= "Bob" (:name (:comment (first rating-list)))))
        (is (= "some comment with a &lt;p&gt;p tag&lt;/p&gt;" (:comment (:comment (first rating-list)))))
        (is (= 5 (:overall (:rating (first rating-list))))))))
  (testing "rate limiting"
    (let [db (create-mock-db)

          app-instance (app db true)
          responses (doall (repeatedly 101 (fn []
                                             (app-instance (-> (request :post "/api/conferences/")
                                                               (body (json/write-str (some-conference-with {:name "some name" :description "some description with a <p>p tag</p>"})))
                                                               (header :content-type "application/json"))))))]
      (is (= 429 (:status (last responses))))))
  (testing "should add, edit and delete a conference"
    (let [db (create-mock-db)]
      (let [response ((app db true) (-> (request :post "/api/conferences/")
                                        (body (json/write-str (some-conference-with {:name "some name" :description "some description with a <p>p tag</p>"})))
                                        (header :content-type "application/json")))]
        (is (= 201 (:status response)))
        (is (.startsWith (get-in response [:headers "Location"]) "/api/conferences/"))
        (let [conference-response (json-body-for db (request :get (get-in response [:headers "Location"])))]
          (is (= "some description with a &lt;p&gt;p tag&lt;/p&gt;" (:description conference-response)))
          (is (= "some name" (:name conference-response)))
          (is (map? (:aggregated-ratings conference-response))))
        (let [conferences (json-body-for db (request :get "/api/conferences"))]
          (is (= 1 (count conferences)))
          (is (= "some description with a &lt;p&gt;p tag&lt;/p&gt;" (:description (first conferences))))
          (is (= "some name" (:name (first conferences))))
          (is (map? (:aggregated-ratings (first conferences))))

          (let [id (:_id (first conferences))
                response ((app db true) (-> (request :put (str "/api/conferences/" id "/edit"))
                                            (body (json/write-str (some-conference-with {
                                                                                         :name "some other name"
                                                                                         :description "some other description"})))
                                            (header :content-type "application/json")))]
            (is (= 200 (:status response)))
            (is (= id (:_id (json-body response))))
            (let [conference (json-body-for db (request :get (str "/api/conferences/" id)))]
              (is (= "some other name" (:name conference)))
              (is (= "some other description" (:description conference)))))
          (let [id (:_id (first conferences))
                response ((app db true) (request :delete (str "/api/conferences/" id)))]
            (is (= 204 (:status response)))))
        (let [conferences (json-body-for db (request :get "/api/conferences"))]
          (is (= 0 (count conferences)))))))

  (testing "conference validation"
    (testing "series too long"
      (let [db (create-mock-db)]
        (let [response ((app db true) (-> (request :post "/api/conferences/")
                                     (body (json/write-str (some-conference-with {:series (s/join (repeat 1000 "x"))})))
                                     (header :content-type "application/json")))]
          (is (= 500 (:status response))))))
    (testing "name too long"
      (let [db (create-mock-db)]
        (let [response ((app db true) (-> (request :post "/api/conferences/")
                                     (body (json/write-str (some-conference-with {:name (s/join (repeat 1000 "x"))})))
                                     (header :content-type "application/json")))]
          (is (= 500 (:status response))))))
    (testing "link too long"
      (let [db (create-mock-db)]
        (let [response ((app db true) (-> (request :post "/api/conferences/")
                                          (body (json/write-str (some-conference-with {:link (s/join (repeat 2000 "x"))})))
                                          (header :content-type "application/json")))]
          (is (= 500 (:status response))))))
    (testing "description too long"
      (let [db (create-mock-db)]
        (let [response ((app db true) (-> (request :post "/api/conferences/")
                                          (body (json/write-str (some-conference-with {:description (s/join (repeat 20000 "x"))})))
                                          (header :content-type "application/json")))]
          (is (= 500 (:status response)))))))
  (testing "rating validation"
    (testing "name too long"
      (let [db (create-mock-db)]
        (let [response ((app db true) (-> (request :post "/api/conferences/someConferenceId/ratings")
                                          (body (json/write-str (some-rating-with :comment {:name (s/join (repeat 1000 "x")) :comment "some comment"})))
                                          (header :content-type "application/json")))]
          (is (= 500 (:status response))))))
    (testing "description too long"
      (let [db (create-mock-db)]
        (let [response ((app db true) (-> (request :post "/api/conferences/someConferenceId/ratings")
                                          (body (json/write-str (some-rating-with :comment {:name "some name" :comment (s/join (repeat 20000 "x"))})))
                                          (header :content-type "application/json")))]
          (is (= 500 (:status response)))))))
  (testing "series suggestions"
    (let [db (create-mock-db)]
      ((app db true) (-> (request :post "/api/conferences/")
                    (body (json/write-str (some-conference-with {:name "some name" :description "some description" :series "some series"})))
                    (header :content-type "application/json")))
      ((app db true) (-> (request :post "/api/conferences/")
                    (body (json/write-str (some-conference-with {:name "some other name" :description "some other description" :series "some series"})))
                    (header :content-type "application/json")))
      ((app db true) (-> (request :post "/api/conferences/")
                    (body (json/write-str (some-conference-with {:name "some other name" :description "some other description" :series "other series"})))
                    (header :content-type "application/json")))

      (testing "should find suggestions for existing series"
        (let [response ((app db true) (request :get "/api/series/suggestions?q=some"))]
          (is (= 200 (:status response)))
          (is (= ["some series"] (json-body response)))))))
  (testing "should fail if incomplete data is written to ratings"
    (let [db (create-mock-db)
          response ((app db true) (-> (request :post "/api/conferences/someConferenceId/ratings")
                                 (body (json/write-str {:some "random value"}))
                                 (header :content-type "application/json")))]
      (is (= 500 (:status response))))))

(deftest backwards-compatibility-test
  (testing "that we get valid ratings even if there is crap in the db"
    (let [db (create-mock-db)
          conference-id (ObjectId.)
          rating-id (ObjectId.)]
      (mc/insert db "conferences" {:_id conference-id :foo :bar})
      (mc/insert db "ratings" {:_id rating-id :conference-id conference-id :foo :bar})
      (let [response ((app db true) (request :get (str "/api/conferences/" conference-id)))]
        (is (= 200 (:status response)))))))

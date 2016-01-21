(ns clj.conference-rating.handler-test
  (:use [conference-rating.handler])
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [ring.mock.request :refer [request body header]]
            [clojure.data.json :as json]
            [monger.collection :as mc]
            [schema.test]
            [conference-rating.testdata :refer [some-rating-with some-rating]])
  (:import (com.github.fakemongo Fongo)
           (org.bson.types ObjectId)))

(use-fixtures :once schema.test/validate-schemas)

(defn json-body [response]
  (json/read-str (:body response) :key-fn keyword))

(defn json-body-for [db request]
  (json-body ((app db) request)))

(defn- create-mock-db []
  (let [fongo (Fongo. "some mock mongodb")]
    (.getDB fongo "crdb")))

(deftest acceptance-test
  (testing "should have an index-page"
    (is (= 200 (:status ((app (create-mock-db)) (request :get "/"))))))
  (testing "should return all ratings of a conference as json"
    (let [db (create-mock-db)
          response ((app db) (-> (request :post "/api/conferences/someConferenceId/ratings")
                                 (body (json/write-str
                                         (some-rating-with :comment {:name "Bob" :comment "some comment"}
                                                           :rating {:overall 5
                                                                    :talks 1
                                                                    :venue 2
                                                                    :networking 3})))
                                 (header :content-type "application/json")))]
      (is (= 201 (:status response)))
      (let [ratings-response ((app db) (request :get "/api/conferences/someConferenceId/ratings"))
            rating-list (json/read-str (:body ratings-response) :key-fn keyword) ]
        (is (= 200 (:status ratings-response) ))
        (is (= 1 (count rating-list)))
        (is (= "Bob" (:name (:comment (first rating-list)))))
        (is (= "some comment" (:comment (:comment (first rating-list)))))
        (is (= 5 (:overall (:rating (first rating-list))))))))
  (testing "should add a conference to the database"
    (let [db (create-mock-db)]
      (let [response ((app db) (-> (request :post "/api/conferences/")
                                   (body (json/write-str {:name "some name" :description "some description"}))
                                   (header :content-type "application/json")))]
        (is (= 201 (:status response)))
        (is (.startsWith (get-in response [:headers "Location"]) "/api/conferences/"))
        (let [conference-response (json-body-for db (request :get (get-in response [:headers "Location"])))]
          (is (= "some description" (:description conference-response)))
          (is (= "some name" (:name conference-response)))
          (is (map? (:aggregated-ratings conference-response))))
      (let [conferences (json-body-for db (request :get "/api/conferences"))]
        (is (= 1 (count conferences)))
        (is (= "some description" (:description (first conferences))))
        (is (= "some name" (:name (first conferences))))
        (is (map? (:aggregated-ratings (first conferences))))))))
  (testing "series suggestions"
    (let [db (create-mock-db)]
      ((app db) (-> (request :post "/api/conferences/")
                    (body (json/write-str {:name "some name" :description "some description" :series "some series"}))
                    (header :content-type "application/json")))
      ((app db) (-> (request :post "/api/conferences/")
                    (body (json/write-str {:name "some other name" :description "some other description" :series "some series"}))
                    (header :content-type "application/json")))
      ((app db) (-> (request :post "/api/conferences/")
                    (body (json/write-str {:name "some other name" :description "some other description" :series "other series"}))
                    (header :content-type "application/json")))

      (testing "should find suggestions for existing series"
        (let [response ((app db) (request :get "/api/series/suggestions?q=some"))]
          (is (= 200 (:status response)))
          (is (= ["some series"] (json-body response)))))))
  (testing "should fail if incomplete data is written to ratings"
    (let [db (create-mock-db)
          response ((app db) (-> (request :post "/api/conferences/someConferenceId/ratings")
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
      (let [response ((app db) (request :get (str "/api/conferences/" conference-id)))]
        (is (= 200 (:status response)))))))

(ns clj.conference-rating.handler-test
  (:use [conference-rating.handler])
  (:require [clojure.test :refer [deftest is testing]]
            [ring.mock.request :refer [request body header]]
            [clojure.data.json :as json]
            [conference-rating.testdata :refer [some-rating-with some-rating]])
  (:import (com.github.fakemongo Fongo)))

(defn json-body-for [db request] (json/read-str (:body ((app db) request)) :key-fn keyword))

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
                                                           :rating {:overall 5})))
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
        (is (= "some name" (:name (first conferences)))))))))
(ns clj.conference-rating.handler-test
  (:use [conference-rating.handler])
  (:require [clojure.test :refer [deftest is testing]]
            [ring.mock.request :refer [request body header]]
            [clojure.data.json :as json])
  (:import (com.github.fakemongo Fongo)))

(defn json-body-for [db request] (json/read-str (:body ((app db) request)) :key-fn keyword))

(defn- create-mock-db []
  (let [fongo (Fongo. "some mock mongodb")]
    (.getDB fongo "crdb")))

(deftest acceptance-test
  (testing "should have an index-page"
    (is (= 200 (:status ((app (create-mock-db)) (request :get "/"))))))
  (testing "should return all ratings of a conference as json"
    (is (= 200 (:status ((app (create-mock-db)) (request :get "/api/conferences/foo/ratings")))))
    (is (= [{:rating-author "Bob" :rating-comment "some comment" :rating-stars 5 :id "1"}
            {:rating-author "Jon" :rating-comment "some comment" :rating-stars 4 :id "2"}]
           (json-body-for (create-mock-db) (request :get "/api/conferences/foo/ratings")))))
  (testing "should add a conference to the database"
    (let [db (create-mock-db)]
      (let [response ((app db) (-> (request :post "/api/conferences/")
                                   (body (json/write-str {:name "some name" :description "some description"}))
                                   (header :content-type "application/json")))]
        (is (= 201 (:status response)))
        (is (.startsWith (get-in response [:headers "Location"]) "/api/conferences/"))
        (let [conference-response (json-body-for db (request :get (get-in response [:headers "Location"])))]
          (is (= "some description" (:description conference-response)))
          (is (= "some name" (:name conference-response))))
      (let [conferences (json-body-for db (request :get "/api/conferences"))]
        (is (= 1 (count conferences)))
        (is (= "some description" (:description (first conferences))))
        (is (= "some name" (:name (first conferences)))))))))
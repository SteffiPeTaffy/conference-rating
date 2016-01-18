(ns conference-rating.db-handler-test
  (:require [monger.collection :as mc]
            [clojure.test :refer :all]
            [conference-rating.db-handler :as dh]
            [conference-rating.testdata :refer :all])
  (:import (com.github.fakemongo Fongo)
           (org.bson.types ObjectId)))

(defn- create-mock-db []
  (let [fongo (Fongo. "some mock mongodb")]
    (.getDB fongo "crdb")))

(defn- contains-in-lazy? [coll key]
  (some #{key} coll))

(def get-conferences-by-series #'dh/get-conferences-by-series)

(deftest by-series-test
  (let [fake-db (create-mock-db)
        test-series "Testseries"
        id1 (ObjectId.)
        id2 (ObjectId.)
        id1-string (.toHexString id1)
        id2-string (.toHexString id2)
        ]

    (mc/insert fake-db "conferences" {:_id id1 :series test-series})
    (mc/insert fake-db "conferences" {:_id id2 :series test-series})
    (mc/insert fake-db "conferences" {:_id 666 :series "different series"})
    (mc/insert fake-db "ratings" (->
                                   (assoc-in (some-rating) [:rating :overall] 4)
                                   (assoc :conference-id id1-string)
                                   (assoc :_id (ObjectId.))
                                   ))
    (mc/insert fake-db "ratings" (->
                                   (assoc-in (some-rating) [:rating :overall] 2)
                                   (assoc :conference-id id2-string)
                                   (assoc :_id (ObjectId.))
                                   ))

    (testing "Should return all ids for a series"
      (let [result (get-conferences-by-series test-series fake-db)]
        (is (contains-in-lazy? result id1-string))
        (is (contains-in-lazy? result id2-string))
        (is (not (contains-in-lazy? result 666)))))

    (testing "Should return all ratings for a series"
      (let [result (dh/get-average-rating-for-series test-series fake-db)]
        (is (= 3
               (get-in result [:overall :avg])))))))



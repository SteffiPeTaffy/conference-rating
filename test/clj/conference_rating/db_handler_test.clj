(ns conference-rating.db-handler-test
  (:require [monger.collection :as mc]
            [clojure.test :refer :all]
            [conference-rating.db-handler :as dh])
  (:import (com.github.fakemongo Fongo)))

(defn- create-mock-db []
  (let [fongo (Fongo. "some mock mongodb")]
    (.getDB fongo "crdb")))

(defn- contains-in-lazy? [coll key]
  (some #{key} coll))

(def get-conferences-by-series #'dh/get-conferences-by-series)

(deftest ids-by-series-test
         (testing "Should return all ids for a series")
         (let [fake-db (create-mock-db)
               test-series "Testseries"]
           (mc/insert fake-db "conferences" {:_id 42 :series test-series})
           (mc/insert fake-db "conferences" {:_id 7411 :series test-series})
           (mc/insert fake-db "conferences" {:_id 666 :series "different series"})
           (let [result (get-conferences-by-series test-series fake-db)]
             (is (contains-in-lazy? result 42))
             (is (contains-in-lazy? result 7411))
             (is (not (contains-in-lazy? result 666))))))

(ns conference-rating.db-handler-test
  (:require [monger.collection :as mc]
            [clojure.test :refer :all]
            [conference-rating.db-handler :as dh :refer [delete-conference-by-id get-conferences-list update-conference-by-id]]
            [conference-rating.testdata :refer :all])
  (:import (com.github.fakemongo Fongo)
           (org.bson.types ObjectId)))

(defn- create-mock-db []
  (let [fongo (Fongo. "some mock mongodb")]
    (.getDB fongo "crdb")))

(def get-conferences-by-series #'dh/get-conferences-by-series)

(deftest by-series-test
  (let [fake-db (create-mock-db)
        test-series "Testseries"
        lower-case-test-series "testseries"
        id1 (ObjectId.)
        id2 (ObjectId.)
        id-deleted (ObjectId.)
        id1-string (.toHexString id1)
        id2-string (.toHexString id2)]

    (mc/insert fake-db "conferences" {:_id id1 :series test-series})
    (mc/insert fake-db "conferences" {:_id id2 :series lower-case-test-series})
    (mc/insert fake-db "conferences" {:_id id-deleted :series test-series :deleted true})
    (mc/insert fake-db "conferences" {:_id 666 :series "different series"})
    (mc/insert fake-db "ratings" (-> (some-rating)
                                     (assoc-in [:rating :overall] 4)
                                     (assoc :conference-id id1-string)
                                     (assoc :_id (ObjectId.))))
    (mc/insert fake-db "ratings" (-> (some-rating)
                                     (assoc-in [:rating :overall] 2)
                                     (assoc :conference-id id2-string)
                                     (assoc :_id (ObjectId.))))

    (testing "Should return all ids for a series"
      (let [result (get-conferences-by-series test-series fake-db)]
        (is (= [id1-string id2-string] result))))

    (testing "Should return empty collection if series is nil"
      (let [result (get-conferences-by-series nil fake-db)]
        (is (empty? result))))

    (testing "Should return all ratings for a series"
      (let [result (dh/get-average-rating-for-series test-series fake-db)]
        (is (= 3 (get-in result [:overall :avg])))))))

(deftest delete-conference-test
  (let [fake-db (create-mock-db)
        id1 (ObjectId.)
        id2 (ObjectId.)
        id1-string (.toHexString id1)]

    (mc/insert fake-db "conferences" {:_id id1 :series "Conference to be deleted"})
    (mc/insert fake-db "conferences" {:_id id2 :series "different series"})
    (mc/insert fake-db "ratings" (-> (some-rating)
                                     (assoc-in [:rating :overall] 2)
                                     (assoc :conference-id id1-string)))

    (delete-conference-by-id id1-string fake-db)

    (testing "that the deleted conference has a delete flag"
      (let [conference (mc/find-one-as-map fake-db "conferences" {:_id id1})]
        (is (:deleted conference))
        (is (= "Conference to be deleted" (:series conference)))))
    (testing "that ratings for the conference remain untouched"
      (let [rating (mc/find-one-as-map fake-db "ratings" {:conference-id id1-string})]
        (is (not (:deleted rating)))
        (is (= 2 (:overall (:rating rating))))))))

(deftest edit-conference-test
  (let [fake-db (create-mock-db)
        id (ObjectId.)
        id-string (.toHexString id)
        original-conference {:_id id :series "Conference to be edited" :name "some name" :description "some description" :foo "bar"}
        edited-conference {:_id id :series "Conference to be edited" :name "some other name" :description "some other description"}]

    (mc/insert fake-db "conferences" original-conference)

    (update-conference-by-id id-string edited-conference fake-db)

    (testing "that the conference has been updated"
      (let [conference (mc/find-one-as-map fake-db "conferences" {:_id id})]
        (is (= edited-conference conference))))))

(deftest get-conference-list-test
  (let [fake-db (create-mock-db)
        id (ObjectId.)
        id-deleted (ObjectId.)]

    (mc/insert fake-db "conferences" {:_id id})
    (mc/insert fake-db "conferences" {:_id id-deleted :deleted true})

    (let [conference-list (get-conferences-list fake-db)
          ids (map :_id conference-list)]
      (testing "that it should get a list of not deleted conferences"
        (is (= [(.toHexString id)] ids))))))


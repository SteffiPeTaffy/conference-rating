(ns conference-rating.db-handler-test
  (:require [monger.collection :as mc]
            [clojure.test :refer :all]
            [conference-rating.db-handler :as dh :refer [
                                                         delete-conference-by-id
                                                         get-all-conferences
                                                         update-conference-by-id
                                                         get-future-conferences
                                                         get-past-conferences]]
            [conference-rating.testdata :refer :all]
            [java-time :as jtime])
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

    (let [conference-list (get-all-conferences fake-db)
          ids (map :_id conference-list)]
      (testing "that it should get a list of not deleted conferences"
        (is (= [(.toHexString id)] ids))))))

(defn- insert-conference [db id today-modificator amount-modificator]
  (let [today (jtime/local-date-time)
        from (jtime/format :iso-date-time (today-modificator today amount-modificator))
        to (jtime/format :iso-date-time (today-modificator today amount-modificator))]
    (mc/insert db "conferences" {:_id id :score 1 :from from :to to})))

(deftest get-future-and-past-conferences-test
  (let [fake-db (create-mock-db)
        id-future-1 (ObjectId.)
        id-future-2 (ObjectId.)
        id-future-3 (ObjectId.)
        id-future-4 (ObjectId.)
        id-past-1 (ObjectId.)
        id-past-2 (ObjectId.)
        id-past-3 (ObjectId.)]

    (insert-conference fake-db id-future-1 jtime/plus (jtime/days 1))
    (insert-conference fake-db id-future-2 jtime/plus (jtime/days 2))
    (insert-conference fake-db id-future-3 jtime/plus (jtime/days 3))
    (insert-conference fake-db id-future-4 jtime/plus (jtime/days 4))

    (insert-conference fake-db id-past-1 jtime/minus (jtime/days 1))
    (insert-conference fake-db id-past-2 jtime/minus (jtime/days 2))
    (insert-conference fake-db id-past-3 jtime/minus (jtime/days 3))

    (testing "should return only future conferences"
      (let [conference-list (get-future-conferences fake-db 1 10)
            ids (map :_id conference-list)]
        (is (= [(.toHexString id-future-1)
                (.toHexString id-future-2)
                (.toHexString id-future-3)
                (.toHexString id-future-4)]
               ids))))
    (testing "should return only past conferences"
      (let [conference-list (get-past-conferences fake-db 1 10)
            ids (map :_id conference-list)]
        (is (= [(.toHexString id-past-1)
                (.toHexString id-past-2)
                (.toHexString id-past-3)]
               ids))))))

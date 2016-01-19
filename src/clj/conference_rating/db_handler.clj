(ns conference-rating.db-handler
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [schema.core :as s]
            [conference-rating.schemas :as schemas]
            [schema.utils :as schema-utils]
            [conference-rating.aggregator :as ag])
  (:import (org.bson.types ObjectId)))

(def mongolab-uri (System/getenv "MONGOLAB_URI"))

(defn clear-id-in-doc [doc]
  (assoc doc :_id (.toHexString (:_id doc))))

(defn connect []
  (if mongolab-uri
    (:db (mg/connect-via-uri mongolab-uri))
    (mg/get-db (mg/connect {:host "127.0.0.1" :port 27017}) "crdb")))

(defn add-conference [conference db]
  (let [series (:series conference)
        upper-case-series (if series (.toUpperCase series) "")
        document (assoc conference :_id (ObjectId.) :series upper-case-series)]
    (mc/insert db "conferences" document)
    (clear-id-in-doc document)))

(s/defn add-rating [rating :- schemas/Rating db]
  (let [document (assoc rating :_id (ObjectId.))]
    (mc/insert db "ratings" document)
    (clear-id-in-doc document)))

(defn get-conferences-list [db]
  (let [list (mc/find-maps db "conferences")]
    (map clear-id-in-doc list)))

(defn get-conference [id db]
  (let [item (mc/find-one-as-map db "conferences" {:_id (ObjectId. ^String id)})]
    (clear-id-in-doc item)))

(defn- only-valid [rating]
  (let [valid (not (schema-utils/error? rating))]
    (if-not valid
      (println "ignoring " rating))
    valid))

(s/defn ^:always-validate get-ratings :- [schemas/Rating] [conference-id db]
  (let [rating-list (mc/find-maps db "ratings" {:conference-id conference-id})
        cleared-ratings (->> rating-list
                             (map clear-id-in-doc)
                             (map schemas/coerce-rating)
                             (filter only-valid))]
    cleared-ratings))

(defn- get-conferences-by-series [series db]
  (->>
    (mc/find-maps db "conferences" {:series series})
    (map :_id)
    (map #(.toHexString %))))

(defn get-average-rating-for-series [series db]
  (let [ids (get-conferences-by-series series db)
        ratings (flatten (map #(get-ratings % db) ids))]
    (ag/aggregate-ratings ratings)))
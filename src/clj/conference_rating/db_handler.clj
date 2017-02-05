(ns conference-rating.db-handler
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :as mq]
            [monger.operators :refer :all]
            [schema.core :as s]
            [conference-rating.schemas :as schemas]
            [schema.utils :as schema-utils]
            [conference-rating.aggregator :as ag])
  (:import (org.bson.types ObjectId)))

(def mongolab-uri (System/getenv "MONGOLAB_URI"))

(defn clear-id-in-doc [doc]
  (assoc doc :_id (.toHexString (:_id doc))))

(defn- only-valid [rating]
  (let [valid (not (schema-utils/error? rating))]
    (if-not valid
      (println "ignoring " rating))
    valid))

(defn connect []
  (if mongolab-uri
    (:db (mg/connect-via-uri mongolab-uri))
    (mg/get-db (mg/connect {:host "127.0.0.1" :port 27017}) "crdb")))

(s/defn ^:always-validate add-conference [conference :- schemas/Conference db]
  (let [document (assoc conference :_id (ObjectId.))]
    (mc/insert db "conferences" document)
    (clear-id-in-doc document)))

(s/defn add-rating [rating :- schemas/Rating db]
  (let [document (assoc rating :_id (ObjectId.))]
    (mc/insert db "ratings" document)
    (clear-id-in-doc document)))

(s/defn add-attendance [attendance :- schemas/Attendance db]
  (let [document (assoc attendance :_id (ObjectId.))]
    (mc/update db "attendances" {:user (:user document) :conference-id (:conference-id document)} {$set {:user (:user document)}} {:upsert true})
    (clear-id-in-doc document)))

(defn get-attendances [conference-id db]
  (let [attendance-list (mq/with-collection db "attendances"
                                            (mq/find {:conference-id conference-id})
                                            (mq/limit 100))
        cleared-attandances (->> attendance-list
                             (map clear-id-in-doc)
                             (map schemas/coerce-attendance)
                             (filter only-valid))]
    cleared-attandances))

(defn get-conferences-list [db]
  (let [list (mq/with-collection db "conferences"
                                 (mq/find {:deleted {$ne true}})
                                 (mq/limit 100))]
    (map clear-id-in-doc list)))

(defn get-conference [id db]
  (let [item (mc/find-one-as-map db "conferences" {:_id (ObjectId. ^String id)})]
    (clear-id-in-doc item)))

(defn delete-conference-by-id [^String id db]
  (mc/update-by-id db "conferences" (ObjectId. id) {$set {:deleted true}}))


(defn update-conference-by-id [^String id conference db]
  (let [object-id (ObjectId. id)
        document (assoc conference :_id object-id)]
    (mc/update-by-id db "conferences" object-id document)
    (clear-id-in-doc document)))

(s/defn ^:always-validate get-ratings :- [schemas/Rating] [conference-id db]
  (let [rating-list (mq/with-collection db "ratings"
                                        (mq/find {:conference-id conference-id})
                                        (mq/limit 100))
        cleared-ratings (->> rating-list
                             (map clear-id-in-doc)
                             (map schemas/coerce-rating)
                             (filter only-valid))]
    cleared-ratings))

(defn- get-conferences-by-series [series db]
  (if-not (nil? series)
    (->> (mc/find-maps db "conferences" {:series {$regex series $options "i"} :deleted {$ne true}})
         (map :_id)
         (map #(.toHexString %)))
    (list)))

(defn get-average-rating-for-series [series db]
    (let [ids (get-conferences-by-series series db)
          ratings (flatten (map #(get-ratings % db) ids))]
      (ag/aggregate-ratings ratings)))
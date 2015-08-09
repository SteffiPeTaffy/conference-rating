(ns conference-rating.db-handler
       (:require [monger.core :as mg]
                 [monger.collection :as mc])
        (:import org.bson.types.ObjectId))

(def mongolab-uri (System/getenv "MONGOLAB_URI"))

(defn clear-id [doc]
  (assoc doc :_id (.toHexString (:_id doc))))

(defn connect []
  (if mongolab-uri
    (:db (mg/connect-via-uri mongolab-uri))
    (mg/get-db (mg/connect {:host "127.0.0.1" :port 27017}) "crdb")))

(defn add-conference [conference db]
  (let [document (assoc conference :_id (ObjectId.))]
    (mc/insert db "conferences" document)
    (clear-id document)))

(defn add-rating [conference-id rating db]
  (let [document (assoc rating :_id (ObjectId.)
                               :conference-id conference-id)]
    (mc/insert db "ratings" document)
    (clear-id document)))

(defn get-conferences-list [db]
  (let [list (mc/find-maps db "conferences")]
    (map clear-id list)))

(defn get-conference [id db]
  (let [item (mc/find-one-as-map db "conferences" {:_id (ObjectId. ^String id)})]
    (println item)
    (clear-id item)))


(defn get-ratings [conference-id db]
  (let [rating-list (mc/find-maps db "ratings" {:conference-id conference-id})]
    (println rating-list)
    (map clear-id rating-list)))
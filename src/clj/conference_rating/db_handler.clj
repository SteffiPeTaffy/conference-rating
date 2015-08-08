(ns conference-rating.db-handler
       (:require [monger.core :as mg]
                 [monger.collection :as mc])
        (:import org.bson.types.ObjectId))

(def mongolab-uri (System/getenv "MONGOLAB_URI"))

(defn connect []
  (if mongolab-uri
    (:db (mg/connect-via-uri mongolab-uri))
    (mg/get-db (mg/connect {:host "127.0.0.1" :port 27017}) "crdb")))

(defn add-conference [conference db]
  (let [document (assoc conference :_id (ObjectId.))]
    (mc/insert db "conferences" document)
    document))

(defn add-rating [conference-id rating db]
  (let [document (assoc rating :_id (ObjectId.)
                               :conference-id conference-id)]
    (mc/insert db "ratings" document)
    document))


(defn clear-id [doc]
  (assoc doc :_id (.toHexString (:_id doc))))

(defn get-conferences-list [db]
  (let [list (mc/find-maps db "conferences")]
    (map clear-id list)))

(defn get-item-name [db coll name]
  {})
;(let [item]
;  (mc/find db coll {:name name} item)
;  item))



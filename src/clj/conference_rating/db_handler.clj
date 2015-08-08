(ns conference-rating.db-handler
       (:require [monger.core :as mg]
                 [monger.collection :as mc])
        (:import org.bson.types.ObjectId))

(def mongolab-uri (System/getenv "MONGOLAB_URI"))

(defn- connect []
  (if mongolab-uri
    (second (mg/connect-via-uri {:uri mongolab-uri }))
    (mg/get-db (mg/connect {:host "127.0.0.1" :port 27017}) "crdb")))

(defn add-conference []
  (let [db (connect)
        document { :_id (ObjectId.) :name "Devoxx" :description "Devoxx is a conference" }]
    (mc/insert db "conferences" document)
    document))

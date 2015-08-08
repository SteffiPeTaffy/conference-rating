(ns conference-rating.db-handler
       (:require [monger.core :as mg]
                 [monger.collection :as mc])
        (:import org.bson.types.ObjectId))

(def mongolab-uri (System/getenv "MONGOLAB_URI"))

(defn- connect []
  (if mongolab-uri
    (mg/connect {:uri mongolab-uri })
    (mg/connect {:host "127.0.0.1" :port 27017})))

(defn add-conference []
  (let [conn (connect)
        db   (mg/get-db conn "crdb")
        document { :_id (ObjectId.) :name "Devoxx" :description "Devoxx is a conference" }]
    (mc/insert db "conferences" document)
    document))

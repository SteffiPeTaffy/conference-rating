(ns conference-rating.db-handler
       (:require [monger.core :as mg]
                 [monger.collection :as mc])
        (:import org.bson.types.ObjectId))


(defn add-conference []
  (let [conn (mg/connect {:host "127.0.0.1" :port 27017})
        db   (mg/get-db conn "crdb")
        document { :_id (ObjectId.) :name "Devoxx" :description "Devoxx is a conference" }]
    (mc/insert db "conferences" document)
    document))

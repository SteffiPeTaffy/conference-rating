(ns conference-rating.testdata
  (:require [schema.core :as s]
            [conference-rating.schemas :as schemas])
  (:import (java.util UUID)))

(defn some-rating-values []
  {:overall    -1
   :talks      -1
   :venue      -1
   :networking -1})

(defn some-rating-values-with [& kv]
  (apply assoc (some-rating-values) kv))

(defn some-rating []
  {:_id           (str (UUID/randomUUID))
   :conference-id "some-conference-id"
   :recommended   false
   :roles         [:dev :devops :qa :ux :pm :ba :sales :recruiting :other]
   :experience    [:rookie :beginner :intermediate :advanced :expert]
   :comment       {:name    "some-name"
                   :comment "some-comment"}
   :rating        (some-rating-values)
   :tags          [:inspiring :informative :entertaining :hires :clients]})

(defn some-rating-with [& kv]
  (apply assoc (some-rating) kv))


(def some-valid-conference {:name        "some name"
                            :series      "some series"
                            :from        "2016-02-06T16:31:03.679"
                            :to          "2016-02-07T16:31:03.679"
                            :description "some description"
                            :link        "http://some-link.com"})

(defn some-conference-with [m]
  (merge some-valid-conference m))
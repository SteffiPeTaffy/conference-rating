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

(s/defn some-rating :- schemas/Rating []
  {:_id           (str (UUID/randomUUID))
   :conference-id "some-conference-id"
   :recommended   false
   :roles         [:dev :devops :qa :ux :pm :ba :sales :recruiting :other]
   :experience    [:rookie :beginner :intermediate :advanced :expert]
   :comment       {:name    "some-name"
                   :comment "some-comment"}
   :rating        (some-rating-values)
   :tags          [:inspiring :informative :entertaining :hires :clients]})

(s/defn some-rating-with :- schemas/Rating [& kv]
  (apply assoc (some-rating) kv))
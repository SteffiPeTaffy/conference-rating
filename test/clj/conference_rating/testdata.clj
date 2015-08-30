(ns conference-rating.testdata
  (:require [schema.core :as s]
            [conference-rating.schemas :as schemas])
  (:import (java.util UUID)))


(s/defn some-rating :- schemas/Rating []
  {:_id           (str (UUID/randomUUID))
   :conference-id "some-conference-id"
   :recommended   false
   :roles         [:dev :devops :qa :ux :pm :ba :sales :recruiting :other]
   :experience    [:rookie :beginner :intermediate :advanced :expert]
   :comment       {:name    "some-name"
                   :comment "some-comment"}
   :rating        {:overall    -1
                   :talks      -1
                   :venue      -1
                   :networking -1}
   :tags          [:inspiring :informative :entertaining :hires :clients]})

(s/defn some-rating-with :- schemas/Rating [& kv]
  (apply assoc (some-rating) kv))
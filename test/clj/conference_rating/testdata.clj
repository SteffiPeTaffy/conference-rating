(ns conference-rating.testdata)


(defn some-rating []
  {:recommended false
   :roles       [:dev :devops :qa :ux :pm :ba :sales :recruiting :other]
   :experience  [:rookie :beginner :intermediate :advanced :expert]
   :comment     {:name    "some-name"
                 :comment "some-comment"}
   :rating      {:overall    -1
                 :talks      -1
                 :venue      -1
                 :networking -1}
   :tags        [:inspiring :informative :entertaining :hires :clients]})

(defn some-rating-with [& kv]
  (apply assoc (some-rating) kv))
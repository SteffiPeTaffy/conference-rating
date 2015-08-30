(ns conference-rating.aggregator
  (:require [schema.core :as s]
            [conference-rating.schemas :refer [AggregateRatings Rating]]
            [clojure.algo.generic.functor :refer [fmap]]
            [conference-rating.schemas :as schemas]))

(defn- aggragate-rating-value [key ratings]
  (let [rating-values (->> ratings
                           (map #(get-in % [:rating key]))
                           (filter #(not= -1 %)))
        count         (count rating-values)]
    (if (pos? count)
      {:count count
       :avg   (/ (reduce + rating-values) count)}
      {:count 0
       :avg   0})))

(defn- count-tags [tag-type key ratings]
  (let [roles-in-ratings (->> ratings
                              (map #(get % key))
                              (flatten)
                              (group-by identity)
                              (fmap count))
        roles (schemas/possible-values tag-type)
        skeleton (->> roles
                      (map #(vector % 0))
                      (into {}))]
    (merge skeleton roles-in-ratings)))

(s/defn ^:always-validate aggregate-ratings :- AggregateRatings
  [ratings :- [Rating]]
  {:number-of-ratings (count ratings)
   :recommendations   (->> ratings
                           (filter :recommended)
                           (count))
   :overall           (aggragate-rating-value :overall ratings)
   :talks             (aggragate-rating-value :talks ratings)
   :venue             (aggragate-rating-value :venue ratings)
   :community         (aggragate-rating-value :networking ratings)
   :roles             (count-tags schemas/Role :roles ratings)
   :experience        (count-tags schemas/Experience :experience ratings)
   :tags              (count-tags schemas/Tags :tags ratings)})


(ns conference-rating.util)

(defn- value-true? [[_ v]]
  (true? v))

(defn checkboxes-to-tag-list [m]
  (->> m
       (filter value-true?)
       (map key)))


(defn display-loading []
  [:div [:h2 "Loading..."]])
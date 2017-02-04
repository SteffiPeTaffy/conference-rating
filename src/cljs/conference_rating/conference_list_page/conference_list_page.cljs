(ns conference-rating.conference-list-page.conference-list-page
  (:require [reagent.core :refer [atom]]
            [conference-rating.view-utils.navbar :as navbar]
            [conference-rating.conference-list-page.conference-list-entry :as list-entry]
            [conference-rating.util :as util]
            [cljs-time.core :as t]))


(defn- upcoming-conference? [conference]
  (t/after? (util/parse-date (:to conference)) (t/now)))

(defn- past-conference? [conference]
  (not (upcoming-conference? conference)))

(defn display-conference-list [conference-list]
  (let [upcoming-conferences (filter upcoming-conference? conference-list)
        past-conferences (filter past-conference? conference-list)]
    [:div {:data-e2e "page-conference-list"}
     (navbar/nav-bar conference-list)
     [:div {:class "container-fluid content-container pad-top"}
      [:h3 "Upcoming conferences"]
      [:div {:class "row"} (map #(list-entry/display-conference-list-item %) (sort-by :to upcoming-conferences))]
      [:h3 "Past conferences"]
      [:div {:class "row"} (map #(list-entry/display-conference-list-item %) (reverse (sort-by :to past-conferences)))]]]))

(defonce displayed-conferences (atom nil))

(defn conferences-page []
  (let [conference-list @displayed-conferences]
    (if-not (nil? conference-list)
      (display-conference-list conference-list)
      (util/display-loading))))
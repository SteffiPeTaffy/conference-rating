(ns conference-rating.conference-list-page.conference-list
  (:require [reagent.core :as reagent :refer [atom]]
            [conference-rating.view-utils.header :as header]
            [conference-rating.conference-list-page.conference-list-entry :as list-entry]
            [conference-rating.util :as util]))



(defn display-conference-list [conference-list]
  [:div
   (header/nav-bar)
   [:div {:class "container-fluid content-container pad-top"}
    (header/add-conference-bar)
    [:div {:class "row"}(map list-entry/display-conference-list-item conference-list)]]])

(defonce displayed-conferences (atom nil))

(defn conferences-page []
  (let [conference-list @displayed-conferences]
    (if (not (nil? conference-list))
      (display-conference-list conference-list)
      (util/display-loading))))
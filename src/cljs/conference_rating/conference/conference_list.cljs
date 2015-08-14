(ns conference-rating.conference.conference-list
   (:require [conference-rating.panel :as panel]
             [conference-rating.header :as header]
             [conference-rating.conference.conference-list-entry :as list-entry]))



(defn display-conference-list [conference-list]
  [:div
   (header/nav-bar)
   [:div {:class "container-fluid content-container pad-top"}
    (header/add-conference-bar)
    [:div {:class "row"}(map list-entry/display-conference-list-item conference-list)]]])

(ns conference-rating.conference-detail-page.conference-detail-page
  (:require [reagent.core :as reagent :refer [atom]]
            [conference-rating.view-utils.header :as header]
            [conference-rating.conference-detail-page.rating-list :as rating-list]
            [conference-rating.conference-detail-page.conference-information :as conference-information]
            [conference-rating.conference-detail-page.aggregated-ratings :as aggregated-ratings]
            [conference-rating.util :as util]))

(defn add-rating-button [conference-id]
  [:div {:class "text-lg-right voice-btn-container"}
   [:a {:class "btn btn-md btn-orange" :href (str "#/conferences/" conference-id "/add-rating")} "give it your voice"]])

(defn display-conference-detail-page [conference ratings]
  [:div
   (header/nav-bar)
   [:div {:class "container-fluid content-container pad-top conference-container"}
    [:div {:class "row"}
     [:div {:class "col-lg-1 col-md-1"}]
     [:div {:class "col-lg-6 col-md-6"}
      (conference-information/display-conference-information conference)
      (if (not (util/is-future-conference? conference))
        (add-rating-button (:_id conference)))]
     [:div {:class "col-lg-4 col-md-4 aggregated-ratings-container"}
      (aggregated-ratings/display-aggregated-ratings (:aggregated-ratings conference))]
     [:div {:class "col-lg-1 col-md-1"}]]
    [:div {:class "row"}
     [:div {:class "col-lg-1 col-md-1"}]
     [:div {:class "col-lg-10 col-md-10"}
      (rating-list/display-rating-list ratings)]]]])

(defonce displayed-conference (atom nil))
(defonce display-ratings (atom nil))

(defn conference-page []
  (let [conference @displayed-conference
       ratings @display-ratings]
    (if (not (nil? conference))
      (display-conference-detail-page conference ratings)
      (util/display-loading))))
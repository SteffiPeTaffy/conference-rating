(ns conference-rating.conference-detail-page.conference-detail-page
  (:require [reagent.core :as reagent :refer [atom]]
            [conference-rating.view-utils.navbar :as navbar]
            [conference-rating.conference-detail-page.rating-list :as rating-list]
            [conference-rating.conference-detail-page.conference-information :as conference-information]
            [conference-rating.conference-detail-page.aggregated-ratings :as aggregated-ratings]
            [conference-rating.util :as util]
            [ajax.core :as ajax]
            [conference-rating.backend :as backend]
            [conference-rating.history :as history]
            [conference-rating.view-utils.conference :as conference-util]))

(defn add-rating-button [conference-id]
  [:div {:class "text-lg-right voice-btn-container"}
   [:a {:class "btn btn-md btn-orange" :href (str "#/conferences/" conference-id "/add-rating") :data-e2e "button-add-new-rating"} "give it your voice"]])

(defn delete-conference [conference]
  (let [delete (js/confirm "If you delete this conference, it will be gone forever!")]
    (if delete
      (ajax/DELETE (str "/api/conferences/" (:_id conference)) {:keywords?       true
                                                                :handler         #(history/redirect-to "/")
                                                                :error-handler   #(js/alert "Could not delete conference.")
                                                                :headers         {:X-CSRF-Token (backend/anti-forgery-token)}}))))

(defn edit-conference [conference]
  (history/redirect-to (str "/conferences/" (:_id conference) "/edit")))

(defn add-action-bar [conference]
  [:div {:class "row cl-light"}
   [:div {:class "action-button-container"}
    [:a {:class "btn btn-sm btn-gray"
         :data-e2e "button-delete-conference"
         :on-click #(delete-conference conference)}
     "delete"]
    [:a {:class "btn btn-sm btn-gray"
         :data-e2e "button-edit-conference"
         :on-click #(edit-conference conference)}
     "edit"]]])

(defn display-conference-detail-page [conference ratings conference-list]
  [:div {:data-e2e "page-conference-detail"}
   (navbar/nav-bar conference-list)
   [:div {:class "container-fluid content-container pad-top conference-container"}
    [:div {:class "row"}
     [:div {:class "col-lg-1 col-md-1"}]
     [:div {:class "col-lg-6 col-md-6"}
      (conference-information/display-conference-information conference)
      (add-action-bar conference)
      (if (not (conference-util/is-future-conference? conference))
        (add-rating-button (:_id conference)))]


      [:div {:class "col-lg-4 col-md-4 aggregated-ratings-container"}
       (aggregated-ratings/display-aggregated-ratings conference)]
     [:div {:class "col-lg-1 col-md-1"}]]
    [:div {:class "row"}
     [:div {:class "col-lg-1 col-md-1"}]
     [:div {:class "col-lg-10 col-md-10"}
      (rating-list/display-rating-list ratings)]]]])

(defonce displayed-conference (atom nil))
(defonce display-ratings (atom nil))
(defonce displayed-conference-list (atom []))

(defn conference-page []
  (let [conference @displayed-conference
        ratings @display-ratings
        conference-list @displayed-conference-list]
    (if-not (nil? conference)
      (display-conference-detail-page conference ratings conference-list)
      (util/display-loading))))
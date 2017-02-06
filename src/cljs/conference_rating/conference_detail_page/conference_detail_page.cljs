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


(defn delete-conference [conference]
  (let [delete (js/confirm "If you delete this conference, it will be gone forever including all of its ratings!")]
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
     "Delete"]
    [:a {:class "btn btn-sm btn-gray"
         :data-e2e "button-edit-conference"
         :on-click #(edit-conference conference)}
     "Edit"]]])

(defonce displayed-conference (atom nil))

(defn display-conference-detail-page [conference ratings conference-list]
  [:div {:data-e2e "page-conference-detail"}
   (navbar/nav-bar conference-list)
   [:div {:class "container-fluid content-container pad-top conference-container"}
    [:div {:class "row"}
     [:div {:class "col-lg-1 col-md-1"}]
     [:div {:class "col-lg-6 col-md-6"}
      (conference-information/display-conference-information conference)
      (add-action-bar conference)
      [:div {:class "btn-container"}
       (conference-util/add-rating-button conference "btn-md" "Give it your voice")]
      [:div {:class "btn-container text-lg-right"}
       (conference-util/attending-button conference "btn-md btn-adrk-gray" (fn [] (backend/load-conference (:_id conference) #(reset! displayed-conference %1))))]
      [:div {:class "text-lg-right"}
       (conference-util/attending-summary-label conference)
       (conference-util/attendees-list-label conference)]]
     [:div {:class "col-lg-4 col-md-4 aggregated-ratings-container"}
       (aggregated-ratings/display-aggregated-ratings conference)]
     [:div {:class "col-lg-1 col-md-1"}]]
    [:div {:class "row"}
     [:div {:class "col-lg-1 col-md-1"}]
     [:div {:class "col-lg-10 col-md-10 marg-top-20"}
      (rating-list/display-rating-list ratings)]]]])

(defonce display-ratings (atom nil))
(defonce displayed-conference-list (atom []))

(defn conference-page []
  (let [conference @displayed-conference
        ratings @display-ratings
        conference-list @displayed-conference-list]
    (if-not (nil? conference)
      (display-conference-detail-page conference ratings conference-list)
      (util/display-loading))))
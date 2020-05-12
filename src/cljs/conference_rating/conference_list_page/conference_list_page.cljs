(ns conference-rating.conference-list-page.conference-list-page
  (:require [reagent.core :refer [atom]]
            [conference-rating.view-utils.navbar :as navbar]
            [conference-rating.conference-list-page.conference-list-entry :as list-entry]
            [conference-rating.util :as util]
            [conference-rating.backend :as backend]))

(defonce displayed-past-conferences (atom nil))
(defonce current-page-past-conferences (atom 1))

(defonce displayed-future-conferences (atom nil))
(defonce current-page-future-conferences (atom 1))

(defn- display-new-conferences-2 [displayed-atom-conferences next-page-conferences]
  (reset! displayed-atom-conferences next-page-conferences))

(defn- load-new-page [backend-load-fn displayed-conferences-atom update-current-page-atom-fn current-page-conferences-atom]
  (swap! current-page-conferences-atom update-current-page-atom-fn)
  (backend-load-fn @current-page-conferences-atom #(display-new-conferences-2 displayed-conferences-atom %1)))

(defn- paginator-banner [backend-load-fn current-page-conferences-atom displayed-conferences-atom data-e2e-suffix]
  [:div {:class "paginator-container"}
   (let [enable-button (> @current-page-conferences-atom 1) previous-page-handler #(if enable-button (load-new-page backend-load-fn displayed-conferences-atom dec current-page-conferences-atom))]
     [:a {:class "btn btn-sm btn-orange" :data-e2e (str "button-previous-page-conferences-" data-e2e-suffix) :on-click previous-page-handler :disabled (not enable-button)} "Previous Page"])
   [:span {:class "current-page" } (str "Page: " @current-page-conferences-atom)]
   [:a {:class "btn btn-sm btn-orange" :data-e2e (str "button-next-page-conferences-" data-e2e-suffix) :on-click #(load-new-page backend-load-fn displayed-conferences-atom inc current-page-conferences-atom)} "Next Page"]
   ])

(defn display-conference-list [future-conferences past-conferences]
  [:div {:data-e2e "page-conference-list"}
     (navbar/nav-bar (conj future-conferences past-conferences))
     [:div {:class "container-fluid content-container pad-top"}
      [:h2 "Upcoming conferences"]
      [:div {:class "row"} (map #(list-entry/display-conference-list-item % future-conferences) (sort-by :to future-conferences))]
      (paginator-banner backend/load-future-conferences current-page-future-conferences displayed-future-conferences "future")
      [:h2 "Past conferences"]
      [:div {:class "row"} (map #(list-entry/display-conference-list-item % past-conferences) (reverse (sort-by :to past-conferences)))]
      (paginator-banner backend/load-past-conferences current-page-past-conferences displayed-past-conferences "past")]
   ])

(defn conferences-page []
  (let [future-conf @displayed-future-conferences past-conf @displayed-past-conferences]
    (if (or future-conf past-conf)
      (display-conference-list future-conf past-conf)
      (util/display-loading))))

(ns conference-rating.conference-list-page.conference-list-page
  (:require [reagent.core :refer [atom]]
            [conference-rating.view-utils.navbar :as navbar]
            [conference-rating.conference-list-page.conference-list-entry :as list-entry]
            [conference-rating.util :as util]
            [conference-rating.backend :as backend]))

(defonce displayed-past-conferences (atom nil))
(defonce total-past-conferences (atom nil))
(defonce current-page-past-conferences (atom 1))

(defonce total-future-conferences (atom nil))
(defonce displayed-future-conferences (atom nil))
(defonce current-page-future-conferences (atom 1))

(defn- load-new-page [backend-load-fn displayed-conferences-atom update-current-page-atom-fn current-page-conferences-atom total-conferences-atom title-id]
  (swap! current-page-conferences-atom update-current-page-atom-fn)
  (backend-load-fn @current-page-conferences-atom #((reset! displayed-conferences-atom %1) (reset! total-conferences-atom %2) (util/scroll-to-id title-id))))

(defn- paginator-banner [backend-load-fn current-page-conferences-atom displayed-conferences-atom total-conferences-atom data-e2e-suffix title-id]
  [:div {:class "paginator-container"}
   (let [enable-button (> @current-page-conferences-atom 1)
         previous-page-handler #(if enable-button (load-new-page backend-load-fn displayed-conferences-atom dec current-page-conferences-atom total-conferences-atom title-id))]
     [:a {:class "btn btn-sm btn-orange" :data-e2e (str "button-previous-page-conferences-" data-e2e-suffix) :on-click previous-page-handler :disabled (not enable-button)} "Previous Page"])
   [:span {:class "current-page" } (str "Page: " @current-page-conferences-atom)]
   (let [number-of-pages (/ @total-conferences-atom backend/CONFERENCES-PER-PAGE)
         enable-page-button (> number-of-pages @current-page-conferences-atom)
         next-page-handler #(if enable-page-button (load-new-page backend-load-fn displayed-conferences-atom inc current-page-conferences-atom total-conferences-atom title-id))]
     [:a {:class "btn btn-sm btn-orange" :data-e2e (str "button-next-page-conferences-" data-e2e-suffix) :on-click next-page-handler :disabled (not enable-page-button)} "Next Page"])
   ])

(defn display-conference-list [future-conferences past-conferences]
  [:div {:data-e2e "page-conference-list"}
     (navbar/nav-bar (conj future-conferences past-conferences))
     (let [title-future-id "title-future-conf"
           title-past-id "title-past-conf"]
       [:div {:class "container-fluid content-container pad-top"}
        [:h2 {:id title-future-id} "Upcoming conferences"]
        [:div {:class "row"} (map #(list-entry/display-conference-list-item % future-conferences) (sort-by :to future-conferences))]
        (paginator-banner backend/load-future-conferences current-page-future-conferences displayed-future-conferences total-future-conferences "future" title-future-id)
        [:h2 {:id title-past-id} "Past conferences"]
        [:div {:class "row"} (map #(list-entry/display-conference-list-item % past-conferences) (reverse (sort-by :to past-conferences)))]
        (paginator-banner backend/load-past-conferences current-page-past-conferences displayed-past-conferences total-past-conferences "past" title-past-id)])
   ])

(defn conferences-page []
  (let [future-conf @displayed-future-conferences past-conf @displayed-past-conferences]
    (if (or future-conf past-conf)
      (display-conference-list future-conf past-conf)
      (util/display-loading))))

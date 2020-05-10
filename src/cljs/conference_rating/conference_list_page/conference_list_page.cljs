(ns conference-rating.conference-list-page.conference-list-page
  (:require [reagent.core :refer [atom]]
            [conference-rating.view-utils.navbar :as navbar]
            [conference-rating.conference-list-page.conference-list-entry :as list-entry]
            [conference-rating.util :as util]
            [conference-rating.backend :as backend]))

(defonce displayed-future-conferences (atom nil))
(defonce displayed-past-conferences (atom nil))
(defonce current-page-future-conferences (atom 1))

(defn- turn-next-page [next-page-conferences]
  (reset! displayed-future-conferences next-page-conferences))

(defn- load-more-conferences []
  (swap! current-page-future-conferences inc)
  (backend/load-future-conferences @current-page-future-conferences turn-next-page))

(defn- turn-previous-page [next-page-conferences]
  (reset! displayed-future-conferences next-page-conferences))

(defn- load-less-conferences []
  (swap! current-page-future-conferences dec)
  (backend/load-future-conferences @current-page-future-conferences turn-previous-page))

(defn- previous-page-handler [enable-button]
  (if enable-button (load-less-conferences)))

(defn display-conference-list [future-conferences past-conferences]
  [:div {:data-e2e "page-conference-list"}
     (navbar/nav-bar (conj future-conferences past-conferences))
     [:div {:class "container-fluid content-container pad-top"}
      [:h3 "Upcoming conferences"]
      [:div {:class "paginator-container"}
       (let [enable-button (> @current-page-future-conferences 1)]
         [:a {:class "btn btn-sm btn-orange" :data-e2e "button-previous-page-future-conferences" :on-click #(previous-page-handler enable-button) :disabled (not enable-button)} "Previous Page"])
       [:span {:class "current-page" } (str "Page: " @current-page-future-conferences)]
       [:a {:class "btn btn-sm btn-orange" :data-e2e "button-next-page-future-conferences" :on-click #(load-more-conferences)} "Next Page"]
       ]
      [:div {:class "row"} (map #(list-entry/display-conference-list-item % future-conferences) (sort-by :to future-conferences))]
      [:h3 "Past conferences"]
      [:div {:class "row"} (map #(list-entry/display-conference-list-item % past-conferences) (reverse (sort-by :to past-conferences)))]]
   ])

(defn conferences-page []
  (let [future-conf @displayed-future-conferences past-conf @displayed-past-conferences]
    (if (or future-conf past-conf)
      (display-conference-list future-conf past-conf)
      (util/display-loading))))

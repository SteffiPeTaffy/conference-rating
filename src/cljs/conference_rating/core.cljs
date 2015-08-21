(ns conference-rating.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [conference-rating.history :as history]
            [conference-rating.conference.add-conference :as add-conference]
            [conference-rating.backend :as backend]
            [conference-rating.conference.conference :as conference]
            [conference-rating.rating.add-rating :as add-rating]
            [conference-rating.conference.conference-list :as conference-list])
    (:import goog.History))

;; -------------------------
;; Views

(defn current-page []
  [:div [(session/get :current-page)]])

(secretary/defroute "/" []
                    (backend/load-conferences #(reset! conference-list/displayed-conferences %1))
                    (session/put! :current-page #'conference-list/conferences-page))

(secretary/defroute "/add-conference" []
                    (session/put! :current-page #'add-conference/add-conference-page))

(secretary/defroute "/conferences/:id" [id]
                    (backend/load-conference id #(reset! conference/displayed-conference %1))
                    (backend/load-conference-ratings id #(reset! conference/ratings %1))
                    (session/put! :current-page #'conference/conference-page))

(secretary/defroute "/conferences/:id/add-rating" [id]
                    (session/put! :conference-id-to-rate id)
                    (session/put! :current-page #'add-rating/add-rating))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (history/hook-browser-navigation!)
  (mount-root))

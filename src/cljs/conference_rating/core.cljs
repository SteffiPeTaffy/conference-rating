(ns conference-rating.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [conference-rating.history :as history]
            [conference-rating.conference.add-conference :as add-conference]
            [conference-rating.home.home :as conference]
            [conference-rating.rating.add-rating :as add-rating])
    (:import goog.History))

;; -------------------------
;; Views

(defn current-page []
  [:div [(session/get :current-page)]])


(secretary/defroute "/" []
                    (conference/load-conferences)
                    (session/put! :current-page #'conference/conferences-page))

(secretary/defroute "/add-conference" []
                    (session/put! :current-page #'add-conference/add-conference-page))

(secretary/defroute "/conferences/:id" [id]
                    (conference/load-conference id)
                    (conference/load-conference-ratings id)
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

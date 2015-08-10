(ns conference-rating.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [conference-rating.history :as history]
            [conference-rating.home :as conference]
            [conference-rating.conference.add-conference :as add-conference]
            [conference-rating.add-rating :as add-rating])
    (:import goog.History))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to conference-rating"]
   [:div [:a {:href "#/conferences/foo"} "go to foo conference"]]])

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

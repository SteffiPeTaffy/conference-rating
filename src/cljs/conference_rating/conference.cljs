(ns conference-rating.conference
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]))

;; -------------------------
;; Views
(defn conference-page []
  [:div [:h2 "Conference"]
   [:span (session/get :conference-id)]])

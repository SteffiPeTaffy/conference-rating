(ns conference-rating.conference
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [ajax.core :as ajax]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]))

(def displayed-conference (atom nil))

;; -------------------------
;; Views
(defn conference-page []
  (if (not (nil? @displayed-conference))
    [:div [:h2 (:conference-name @displayed-conference)]]
    [:div [:h2 "Loading..."]]))


;; -------------------------
;; Requests
(defn load-conference [id]
  (ajax/GET (str "/api/conferences/" id) {:handler #(reset! displayed-conference %1)
                                          :error-handler #(js/alert (str "conference not found" %1))
                                          :response-format :json
                                          :keywords? true}))

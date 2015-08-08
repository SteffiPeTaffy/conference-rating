(ns conference-rating.conference
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [ajax.core :as ajax]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]))

(def displayed-conference (atom nil))

;; -------------------------
;; Requests
(defn load-conference [id]
  (if (and (not (nil? id)) (not= (:id @displayed-conference) id))
    (ajax/GET (str "/api/conferences/" id) {:handler #(reset! displayed-conference %1)
                                            :error-handler #(js/alert (str "conference not found" %1))
                                            :response-format :json
                                            :keywords? true}))
    )


;; -------------------------
;; Views
(defn conference-page []
  (load-conference (session/get :conference-id))
  (let [conference @displayed-conference]
    (if (not (nil? conference))
      [:div [:h2 (:conference-name conference)]]
      [:div [:h2 "Loading..."]])))

(ns conference-rating.conference
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [ajax.core :as ajax]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]))

(def displayed-conference (atom nil))
(def conferences (atom nil))

;; -------------------------
;; Requests
(defn load-conference [id]
  (ajax/GET (str "/api/conferences/" id) {:handler #(reset! displayed-conference %1)
                                          :error-handler #(js/alert (str "conference not found" %1))
                                          :response-format :json
                                          :keywords? true}))

(defn load-conferences []
  (ajax/GET "/api/conferences" {:handler #(reset! conferences %1)
                                :error-handler #(js/alert (str "conferences not found" %1))
                                :response-format :json
                                :keywords? true}))


;; -------------------------
;; Views
(defn conference-page []
  (let [conference @displayed-conference]
    (if (not (nil? conference))
      [:div [:h2 (:conference-name conference)]
       [:p (:conference-description conference)]]
      [:div [:h2 "Loading..."]])))

(defn conference-item [conference-list-entry]
  [:li {:key (:id conference-list-entry)}
   [:a {:href (str "#/conferences/" (:id conference-list-entry))} (:conference-name conference-list-entry)]])

(defn conference-items [conference-list]
  [:ul (map conference-item conference-list)])

(defn conferences-page []
  (let [conference-list @conferences]
    (if (not (nil? conference-list))
      [:div [:h2 "Conferences"] (conference-items conference-list)]
      [:div [:h2 "Loading..."]])))

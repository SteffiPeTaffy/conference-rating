(ns conference-rating.backend
  (:require [ajax.core :as ajax]
            [conference-rating.history :as history]))

(defn load-conference [id success-handler]
  (ajax/GET (str "/api/conferences/" id) {:handler success-handler
                                          :error-handler #(js/alert (str "conference not found" %1))
                                          :response-format :json
                                          :keywords? true}))

(defn load-conference-ratings [id success-handler]
  (ajax/GET (str "/api/conferences/" id "/ratings") {:handler success-handler
                                                     :error-handler #(js/alert (str "ratings not found" %1))
                                                     :response-format :json
                                                     :keywords? true}))

(defn load-conferences [success-handler]
  (ajax/GET "/api/conferences" {:handler success-handler
                                :error-handler #(js/alert (str "conferences not found" %1))
                                :response-format :json
                                :keywords? true}))

(defn load-series-suggestions [q success-handler]
  (ajax/GET (str "/api/series/suggestions?q=" q)
            {:handler         success-handler
             :response-format :json
             :keywords? true}))

(defn anti-forgery-token []
  js/_anti_forgery_token)

(defn add&edit-conference [conference-data url ajax-fn]
  (ajax-fn url {:params          conference-data
                                  :format          :json
                                  :response-format :json
                                  :keywords?       true
                                  :handler         #(let [conference-id (:_id %)]
                                                     (history/redirect-to (str "/conferences/" conference-id)))
                                  :error-handler   #(js/alert (str "could not create conference" %1))
                                  :headers         {:X-CSRF-Token (anti-forgery-token)}}))

(defn add-conference [conference-data]
  (add&edit-conference conference-data "/api/conferences/" ajax/POST))

(defn edit-conference [conference-data]

  (println conference-data)

  (add&edit-conference conference-data (str "/api/conferences/" (:id conference-data) "/edit") ajax/PUT))
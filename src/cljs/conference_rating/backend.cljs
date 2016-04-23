(ns conference-rating.backend
  (:require [ajax.core :as ajax]))

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

(defn reload-conferences [conferences-atom]
  (load-conferences #(reset! conferences-atom %1)))



(defn anti-forgery-token []
  js/_anti_forgery_token)
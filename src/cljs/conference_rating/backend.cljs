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

(ns conference-rating.backend
  (:require [ajax.core :as ajax]
            [conference-rating.home.home :as home]))

(defn load-conference [id]
  (ajax/GET (str "/api/conferences/" id) {:handler #(reset! home/displayed-conference %1)
                                          :error-handler #(js/alert (str "conference not found" %1))
                                          :response-format :json
                                          :keywords? true}))

(defn load-conference-ratings [id]
  (ajax/GET (str "/api/conferences/" id "/ratings") {:handler #(reset! home/ratings %1)
                                                     :error-handler #(js/alert (str "ratings not found" %1))
                                                     :response-format :json
                                                     :keywords? true}))

(defn load-conferences []
  (ajax/GET "/api/conferences" {:handler #(reset! home/conferences %1)
                                :error-handler #(js/alert (str "conferences not found" %1))
                                :response-format :json
                                :keywords? true}))

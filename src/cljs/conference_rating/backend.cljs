(ns conference-rating.backend
  (:require [ajax.core :as ajax]
            [conference-rating.history :as history]
            [conference-rating.util :as util]))

(defn- unsanitize [conference-data]
  (assoc conference-data :name (util/unescape (:name conference-data))
                         :series (util/unescape (:series conference-data))
                         :description (util/unescape (:description conference-data))))

(defn ajaxless-load-conference [id success-handler ajax-fn]
  (ajax-fn (str "/api/conferences/" id) {:handler (fn [conference] (success-handler (unsanitize conference)))
                                          :error-handler #(js/alert "Conference not found.")
                                          :response-format :json
                                          :keywords? true}))

(defn load-conference [id success-handler]
  (ajaxless-load-conference id success-handler ajax/GET))

(defn load-conference-ratings [id success-handler]
  (ajax/GET (str "/api/conferences/" id "/ratings") {:handler success-handler
                                                     :error-handler #(js/alert "Ratings not found.")
                                                     :response-format :json
                                                     :keywords? true}))

(defn ajaxless-load-conferences [success-handler ajax-fn]
  (ajax-fn "/api/conferences" {:handler (fn [conferences] (success-handler (map unsanitize conferences)))
                                :error-handler #(js/alert "Conferences not found.")
                                :response-format :json
                                :keywords? true}))

(defn load-conferences [success-handler]
  (ajaxless-load-conferences success-handler ajax/GET))

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
                                  :error-handler   #(js/alert "Could not create conference. Please make sure to be logged into okta and fill in all required fields.")
                                  :headers         {:X-CSRF-Token (anti-forgery-token)}}))

(defn add-conference [conference-data]
  (add&edit-conference conference-data "/api/conferences/" ajax/POST))

(defn edit-conference [id]
  (fn
    [conference-data]
  (add&edit-conference conference-data (str "/api/conferences/" id "/edit") ajax/PUT)))

(defn load-user-info [success-handler]
  (ajax/GET "/api/user/identity" {:handler         success-handler
                                  :response-format :json
                                  :keywords?       true}))

(defn attend-conference [conference-id success-handler]
  (ajax/POST (str "/api/conferences/" conference-id "/attendance/self") {:handler       success-handler
                                                                         :error-handler #(js/alert "Attending did not work.")
                                                                         :headers       {:X-CSRF-Token (anti-forgery-token)}}))

(defn unattend-conference [conference-id success-handler]
  (ajax/POST (str "/api/conferences/" conference-id "/unattendance/self") {:handler       success-handler
                                                                         :error-handler #(js/alert "Unattending did not work.")
                                                                         :headers       {:X-CSRF-Token (anti-forgery-token)}}))

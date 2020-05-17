(ns conference-rating.backend
  (:require [ajax.core :as ajax]
            [conference-rating.history :as history]
            [conference-rating.util :as util]))

(defn- unsanitise-location [location]
  (if (not (nil? location))
    (update location :name util/unescape)))

(defonce CONFERENCES-PER-PAGE 9)

(defn- unsanitize [conference-data]
  (let [location-escaped-data (unsanitise-location conference-data)]
    (-> location-escaped-data
        (update :name util/unescape)
        (update :series util/unescape)
        (update :description util/unescape)
        (update :location unsanitise-location))))

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

(defn ajaxless-load-conferences [success-handler relative-path ajax-fn]
  (ajax-fn relative-path {:handler (fn [{:keys [items, total-items]}] (success-handler (map unsanitize items) total-items))
                                :error-handler #(js/alert "There were problems loading the conferences")
                                :response-format :json
                                :keywords? true}))

(defn load-conferences [success-handler]
  (ajaxless-load-conferences success-handler "/api/conferences" ajax/GET))

(defn load-future-conferences [current-page success-handler]
  (ajaxless-load-conferences success-handler (str "/api/conferences/future?current-page=" current-page "&per-page=" CONFERENCES-PER-PAGE) ajax/GET))

(defn load-past-conferences [current-page success-handler]
  (ajaxless-load-conferences success-handler (str "/api/conferences/past?current-page=" current-page "&per-page=" CONFERENCES-PER-PAGE) ajax/GET))

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

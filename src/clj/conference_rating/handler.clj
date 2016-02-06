(ns conference-rating.handler
  (:require [compojure.core :refer [GET POST context defroutes routes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults secure-api-defaults]]
            [ring.util.response :refer [created response redirect]]
            [hiccup.core :refer [html]]
            [ring.middleware.json :as json]
            [hiccup.page :refer [include-js include-css]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]
            [environ.core :refer [env]]
            [conference-rating.db-handler :as db]
            [conference-rating.aggregator :as aggregator]
            [schema.core :as s]
            [ring.middleware.okta :as okta]
            [schema.coerce :as coerce]
            [clojure.string :as string]
            [onelog.core :as onelog]
            [conference-rating.schemas :as schemas])
  (:use ring.middleware.anti-forgery))

(defn home-page []
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     [:title "conference voices"]
     (include-css "thirdparty/bootstrap-3.3.5/css/bootstrap.min.css")
     (include-css "thirdparty/bootstrap-3.3.5/css/bootstrap-theme.min.css")
     (include-css "css/reagent-forms.css")
     (include-css "css/site.css")
     [:link {:rel "icon" :type "image/png" :href "img/favicon.png"}]
     [:script (str "_anti_forgery_token=\"" *anti-forgery-token* "\"")]]
    [:body {:class "bg-body"}
     [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]]
     (include-js "thirdparty/jquery-2.1.4.min.js")
     (include-js "thirdparty/bootstrap-3.3.5/js/bootstrap.min.js")
     (include-js "js/app.js")]]))


(defn get-conference [conference-id db]
  (let [conference-info   (db/get-conference conference-id db)
        ratings           (db/get-ratings conference-id db)
        aggregate-ratings (aggregator/aggregate-ratings ratings)
        ratings-of-series (db/get-average-rating-for-series (:series conference-info) db)]
    (->
      conference-info
      (assoc  :aggregated-ratings aggregate-ratings)
      (assoc :average-series-rating ratings-of-series))))

(defn get-conference-ratings [conference-id db]
  (response
    (db/get-ratings conference-id db)))

(defn get-conferences [db]
  (let [conferences (db/get-conferences-list db)
        complete-confernces (->> conferences
                                (map :_id)
                                (map #(get-conference % db)))]
    complete-confernces))

(s/defn add-conference [conference :- schemas/Conference db]
  (let [add-result (db/add-conference conference db)
        id         (:_id add-result)]
    (created (str "/api/conferences/" id) add-result)))

(def parse-rating (coerce/coercer schemas/Rating coerce/json-coercion-matcher))

(defn add-rating [conference-id raw-rating db]
  (let [complete   (assoc raw-rating :conference-id conference-id)
        rating     (parse-rating complete)
        add-result (db/add-rating rating db)
        id         (:_id add-result)]
    (created (str "/api/conferences/" conference-id "/ratings/" id) add-result)))

(defn matches-series [q]
  (fn [series]
    (.contains (string/lower-case series)
               (string/lower-case q))))

(defn series-suggestions [db q]
  (->> (db/get-conferences-list db)
       (map :series)
       (filter (complement string/blank?))
       (filter (matches-series q))
       (distinct)))

(defn- with-anti-forgery [handler]
  (if (env :dev)
    handler
    (wrap-anti-forgery handler)))

(defn anti-forgery-routes [db]
  (with-anti-forgery
    (routes
      (GET "/api/conferences" [] (response (get-conferences db)))
      (GET "/api/conferences/:id" [id] (response (get-conference id db)))
      (GET "/api/conferences/:id/ratings" [id] (get-conference-ratings id db))
      (POST "/api/conferences/:id/ratings" [id :as request] (add-rating id (:body request) db))
      (POST "/api/conferences/" request (add-conference (:body request) db))
      (GET "/api/series/suggestions" {params :params} (response (series-suggestions  db (:q params))))
      (GET "/" [] (home-page)))))

(defn create-routes [db]
  (routes
    (context "" [] (anti-forgery-routes db))
    (resources "/")
    (GET "/css/reagent-forms.css" [] (response (-> "reagent-forms.css"
                                                   clojure.java.io/resource
                                                   slurp)))
    okta/okta-routes
    (GET "/login" [] (redirect "/"))
    (not-found "Not Found")))

(defn ring-settings [ssl-redirect-disabled]
  (-> secure-api-defaults
      (assoc-in [:security :ssl-redirect] (not ssl-redirect-disabled))
      (assoc-in [:security :frame-options] :deny)
      (assoc :proxy true)))

(defn prevent-open-redirect-through-relay-state [handler]
  (fn [request]
    (let [clean-request (-> request
                            (assoc-in [:params :RelayState] "/")
                            (assoc-in [:form-params :RelayState] "/"))]
      (println "request coming in: " request)
      (println "clean request coming in: " clean-request)
      (handler clean-request))))

(defn wrap-dont-show-error-page [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (onelog/error (onelog/throwable e))
        {:status 500 :body "An error occurred" }))))

(defn app [db ssl-redirect-disabled]
  (let [handler (-> (create-routes db)
                    (prevent-open-redirect-through-relay-state)
                    (wrap-defaults (ring-settings ssl-redirect-disabled))
                    (json/wrap-json-response)
                    (json/wrap-json-body {:keywords? true}))]
    (if (env :dev)
      (-> handler
          wrap-exceptions
          wrap-reload)
      (-> handler
          wrap-dont-show-error-page
          ))))

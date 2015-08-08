(ns conference-rating.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults api-defaults]]
            [ring.util.response :refer [created response]]
            [hiccup.core :refer [html]]
            [ring.middleware.json :as json]
            [hiccup.page :refer [include-js include-css]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]
            [environ.core :refer [env]]
            [conference-rating.db-handler :as db]
            ))

(def home-page
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     (include-css "css/site.css")
     (include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css")
     (include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css")]
    [:body
     [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]]
     (include-js "js/app.js")
     (include-js "https://code.jquery.com/jquery-2.1.4.min.js")
     (include-js "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js")]]))

(defn get-conference [conference-id]
  (response
    {:conference-name "Some Conference"
     :conference-description (str "Some Conference Description with id " conference-id)
     :id conference-id}))

(defn get-conference-ratings [conference-id]
  (response
    [{:rating-author "Bob" :rating-comment "some comment" :rating-stars 5 :id "1"}
     {:rating-author "Jon" :rating-comment "some comment" :rating-stars 4 :id "2"}]))

(defn get-conferences []
  (response
    [{:conference-name "Conference 1" :id "1"}
     {:conference-name "Conference 2" :id "2"}]))

(defn add-conference [conference]
  (let [add-result (db/add-conference conference)
        id         (.toHexString (:_id add-result))]
    (created (str "/api/conferences/" id))))

(defroutes routes
           (GET "/" [] home-page)
           (GET "/api/conferences" [] (get-conferences))
           (GET "/api/conferences/:id" [id] (get-conference id))
           (GET "/api/conferences/:id/ratings" [id] (get-conference-ratings id))
           (POST "/api/conferences/" request (do
                                                 (println "body: " )
                                                 (add-conference (:body request))))
           (resources "/")
           (not-found "Not Found"))

(def app
  (let [handler (-> #'routes
                    (wrap-defaults api-defaults)
                    (json/wrap-json-response)
                    (json/wrap-json-body {:keywords? true}))]
    (if (env :dev) (-> handler
                       wrap-exceptions
                       wrap-reload)
                   handler)))

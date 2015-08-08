(ns conference-rating.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults api-defaults]]
            [ring.util.response :refer [response]]
            [hiccup.core :refer [html]]
            [ring.middleware.json :as json]
            [hiccup.page :refer [include-js include-css]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]
            [environ.core :refer [env]]))

(def home-page
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     (include-css "css/site.css")]
    [:body
     [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]]
     (include-js "js/app.js")]]))

(defn get-conference [conference-id]
  (response
    {:conference-name "Some Conference"
     :id conference-id}))

(defroutes routes
           (GET "/" [] home-page)
           (GET "/api/conferences/:id" [id] (get-conference id))
           (resources "/")
           (not-found "Not Found"))

(def app
  (let [handler (-> #'routes
                    (wrap-defaults api-defaults)
                    (json/wrap-json-response)
                    (json/wrap-json-body))]
    (if (env :dev) (-> handler
                       wrap-exceptions
                       wrap-reload)
                   handler)))

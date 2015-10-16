(ns conference-rating.handler
  (:require [compojure.core :refer [GET POST defroutes routes]]
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
            [conference-rating.aggregator :as aggregator]
            [schema.core :as s]
            [ring.middleware.okta :as okta]
            [schema.coerce :as coerce]
            [conference-rating.schemas :as schemas]))

(def home-page
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
     (include-css "css/site.css")]
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
        merged            (assoc conference-info :aggregated-ratings aggregate-ratings)]
    merged))

(defn get-conference-ratings [conference-id db]
  (response
    (db/get-ratings conference-id db)))

(defn get-conferences [db]
  (let [conferences (db/get-conferences-list db)
        complete-confernces (->> conferences
                                (map :_id)
                                (map #(get-conference % db)))]
    complete-confernces))

(s/defn add-conference [conference db]
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

(defn create-routes [db]
  (routes
   (GET "/api/conferences" [] (response (get-conferences db)))
   (GET "/api/conferences/:id" [id] (response (get-conference id db)))
   (GET "/api/conferences/:id/ratings" [id] (get-conference-ratings id db))
   (POST "/api/conferences/:id/ratings" [id :as request] (add-rating id (:body request) db))
   (POST "/api/conferences/" request (add-conference (:body request) db))
   (resources "/")
   (GET "/css/reagent-forms.css" [] (response (-> "reagent-forms.css"
                                                  clojure.java.io/resource
                                                  slurp)))
   okta/okta-routes
   (GET "/login" request {:headers {"Content-Type" "text/plain"}
                          :body (str "Hello Okta: " request)})
   (GET "/" [] home-page)
   (not-found "Not Found")))

(defn app [db]
  (let [handler (-> (create-routes db)
                    (wrap-defaults api-defaults)
                    (json/wrap-json-response)
                    (json/wrap-json-body {:keywords? true}))]
    (if (env :dev) (-> handler
                       wrap-exceptions
                       wrap-reload)
                   handler)))

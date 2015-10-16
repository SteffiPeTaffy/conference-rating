(ns conference-rating.server
  (:require [conference-rating.handler :refer [app]]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [conference-rating.db-handler :as db-handler]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.logger :as ring-logger]
            [ring.middleware.session :as session]
            [ring.middleware.session.cookie :as cookie]
            [ring.middleware.okta :refer [wrap-okta okta-routes]])
  (:gen-class))

(defn do-wrap-okta [handler okta-active]
  (if okta-active
    (wrap-okta handler {:okta-home "https://dev-133267-admin.oktapreview.com/" :okta-config-location (io/resource "okta-ci-config.xml")})
    handler))

 (defn -main [& args]
   (let [port (Integer/parseInt (or (env :port) "3000"))
         okta-active (= "--okta-active" (first args))
         app (-> (app (db-handler/connect))
                 (do-wrap-okta okta-active)
                 (ring-logger/wrap-with-logger)
                 (session/wrap-session)
                 )]
     (run-jetty app {:port port :join? false})))

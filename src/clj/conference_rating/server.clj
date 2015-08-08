(ns conference-rating.server
  (:require [conference-rating.handler :refer [app]]
            [environ.core :refer [env]]
            [conference-rating.db-handler :as db-handler]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

 (defn -main [& args]
   (let [port (Integer/parseInt (or (env :port) "3000"))]
     (run-jetty (app (db-handler/connect)) {:port port :join? false})))

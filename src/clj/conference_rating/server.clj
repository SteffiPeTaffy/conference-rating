(ns conference-rating.server
  (:require [conference-rating.handler :refer [app]]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [clojure.tools.cli :as cli]
            [clojure.string :as string]
            [conference-rating.db-handler :as db-handler]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.logger :as ring-logger]
            [ring.middleware.session :as session]
            [ring.middleware.session.cookie :as cookie]
            [ring.middleware.okta :refer [wrap-okta okta-routes]])
  (:gen-class))

(def cli-options
  ;; An option with a required argument
  [["-o" "--okta-active"]
   ["-e" "--environment ENV" "Environment name, e.g. ci" :default "local"]
   ["-oh" "--okta-home OKTA_HOME" "URL of Okta" :default "https://dev-133267-admin.oktapreview.com/"]
   ["-h" "--help"]])

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))


(defn do-wrap-okta [handler okta-active env okta-home]
  (let [config-res (io/resource (str "okta-" env "-config.xml"))]
    (println "initialize okta?" okta-active " config location: " config-res)
    (if okta-active
      (wrap-okta handler {:okta-home okta-home :okta-config config-res})
      handler)))

(defn start-server [port okta-active env okta-home]
  (let [app (-> (app (db-handler/connect))
                (do-wrap-okta okta-active env okta-home)
                (ring-logger/wrap-with-logger)
                (session/wrap-session))]
    (run-jetty app {:port port :join? false})))

(defn usage [summary]
  (str summary))

(defn -main [& args]
   (let [port (Integer/parseInt (or (env :port) "3000"))
         {:keys [options _ errors summary]} (cli/parse-opts args cli-options)]
     (cond
       (:help options) (exit 0 (usage summary))
       errors (exit 1 (error-msg errors)))
     (start-server port (:okta-active options) (:environment options) (:okta-home options))))

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
            [ring.middleware.okta :refer [wrap-okta okta-routes]]
            [monger.ring.session-store :refer [session-store]])
  (:gen-class))

(def development-api-key "AIzaSyAVDKe5pERAQwk_E8Ayv-dbvbuJZrGLvaY")

(def cli-options
  ;; An option with a required argument
  [["-o" "--okta-active"]
   ["-e" "--environment ENV" "Environment name, e.g. ci" :default "local"]
   ["-k" "--google-api-key KEY" "Google API Key, defaults to development key" :default development-api-key]
   ["-oh" "--okta-home OKTA_HOME" "URL of Okta" :default "https://dev-133267-admin.oktapreview.com/"]
   ["-sd" "--ssl-redirect-disabled"]
   ["-h" "--help"]])

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))


(defn do-wrap-okta [handler env okta-home forced-test-user]
  (let [config-res (io/resource (str "okta-" env "-config.xml"))]
    (println "initialize okta?" (nil? forced-test-user) " config location: " config-res)
    (wrap-okta handler {:okta-home okta-home
                        :okta-config config-res
                        :force-user forced-test-user})))

(defn start-server [port env okta-home ssl-redirect-disabled api-key forced-test-user]
  (let [db  (db-handler/connect)
        app (-> (app db ssl-redirect-disabled api-key)
                (do-wrap-okta env okta-home forced-test-user)
                (ring-logger/wrap-with-logger)
                (session/wrap-session {:store (session-store db "sessions")}))]
    (run-jetty app {:port port :join? false})))

(defn usage [summary]
  (str summary))

(defn -main [& args]
   (let [port (Integer/parseInt (or (env :port) "3000"))
         {:keys [options _ errors summary]} (cli/parse-opts args cli-options)]
     (cond
       (:help options) (exit 0 (usage summary))
       errors (exit 1 (error-msg errors)))
     (start-server port
                   (:environment options)
                   (:okta-home options)
                   (:ssl-redirect-disabled options)
                   (:google-api-key options)
                   (if (:okta-active options) nil "some@testuser.com"))))

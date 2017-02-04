(defproject conference-rating "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs"]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ch.qos.logback/logback-classic "1.1.1"]
                 [ring-server "0.4.0"]
                 [cljsjs/react "0.13.3-1"]
                 [reagent "0.5.0"]
                 [reagent-forms "0.5.5"]
                 [reagent-utils "0.1.5"]
                 [cljs-ajax "0.3.14"]
                 [org.clojure/algo.generic "0.1.2"]
                 [com.andrewmcveigh/cljs-time "0.3.11"]
                 [prismatic/schema "0.4.4"]
                 [ring "1.4.0"]
                 [org.clojure/tools.cli "0.3.3"]
                 [ring/ring-defaults "0.1.5"]
                 [ring-okta "0.1.7-SNAPSHOT"]
                 [ring.middleware.logger "0.5.0"]
                 [ring/ring-json "0.4.0"]
                 [ring-ratelimit "0.2.2"]
                 [prone "0.8.2"]
                 [compojure "1.4.0"]
                 [hiccup "1.0.5"]
                 [environ "1.0.0"]
                 [org.clojure/clojurescript "0.0-3308" :scope "provided"]
                 [secretary "1.2.3"]
                 [com.novemberain/monger "3.0.0-rc2"]
                 [cljsjs/typeahead-bundle "0.11.1-1"]
                 [cljsjs/jquery "2.2.2-0"]
                 [cljsjs/google-maps "3.18-1"]
                 [clj-webdriver "0.7.2"]
                 [org.seleniumhq.selenium/selenium-java "2.47.1"]]

  :repositories [["localrepo" "https://raw.githubusercontent.com/Hendrick/ring-okta/master/maven_repository"]]


  :plugins [[lein-environ "1.0.0"]
            [lein-asset-minifier "0.2.2"]]

  :ring {:handler conference-rating.handler/app
         :uberwar-name "conference-rating.war"}

  :min-lein-version "2.5.0"

  :uberjar-name "conference-rating.jar"

  :main conference-rating.server

  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :app :compiler :output-dir]
                                    [:cljsbuild :builds :app :compiler :output-to]]

  :test-selectors {:default (constantly true)
                   :unit (complement :functional)
                   :functional :functional}

  :minify-assets
  {:assets
    {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild {:builds {:app {:source-paths ["src/cljs"]
                             :compiler {:output-to     "resources/public/js/app.js"
                                        :output-dir    "resources/public/js/out"
                                        :asset-path   "js/out"
                                        :optimizations :none
                                        :pretty-print  true}}}}
  :less {:source-paths ["src/less"]
         :target-path "resources/public/css"}

  :profiles {:dev {:repl-options {:init-ns conference-rating.repl
                                  :nrepl-middleware []}

                   :dependencies [[ring/ring-mock "0.2.0"]
                                  [ring/ring-devel "1.4.0"]
                                  [com.github.fakemongo/fongo "2.0.1"]
                                  [lein-figwheel "0.3.7"]
                                  [org.clojure/tools.nrepl "0.2.10"]
                                  [pjstadig/humane-test-output "0.7.0"]]

                   :source-paths ["env/dev/clj" "src/clj" "src/cljs" "test/clj" "test/cljs"]
                   :plugins [[lein-figwheel "0.3.7"]
                             [lein-cljsbuild "1.0.6"]
                             [lein-kibit "0.1.2"]
                             [com.cemerick/clojurescript.test "0.3.3"]
                             [lein-less "1.7.2"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :figwheel {:http-server-root "public"
                              :server-port 3449
                              :nrepl-port 7002
                              :css-dirs ["resources/public/css"]
                              :ring-handler conference-rating.handler/app}

                   :env {:dev true}

                   :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]
                                              :compiler {:main "conference-rating.dev"
                                                         :source-map true}}
                                        :test {:source-paths ["src/cljs"  "test/cljs"]
                                               :compiler {:output-to "target/test.js"
                                                          :optimizations :whitespace
                                                          :pretty-print true}}}
                               :test-commands {"unit" ["phantomjs" :runner
                                                       "test/vendor/es5-shim.js"
                                                       "test/vendor/es5-sham.js"
                                                       "test/vendor/console-polyfill.js"
                                                       "resources/public/thirdparty/jquery-2.1.4.min.js"
                                                       "target/test.js"]}}}

             :uberjar {:hooks [leiningen.less leiningen.cljsbuild]
                       :env {:production true}
                       :aot :all
                       :omit-source true
                       :cljsbuild {:jar true
                                   :builds {:app
                                             {:source-paths ["env/prod/cljs"]
                                              :compiler
                                              {; don't use advanced optimizations here without checking that jumping to search results still works (#39),
                                               ; apparently agressive optimizations optimize bindings for typeahead:select away as dead code
                                               :optimizations :advanced
                                               :pretty-print false}}}}}})

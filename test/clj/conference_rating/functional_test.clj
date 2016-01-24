(ns conference-rating.functional-test
  (:require [clj-webdriver.taxi :as taxi]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [conference-rating.server :as server]))

(taxi/set-driver! {:browser :chrome})


(defn start-server []
  (server/start-server 4000 false {}))

(defn stop-server [server]
  (.stop server))

(defn once-fixture [tests]
  (let [server (start-server)]
    (tests)
    (taxi/quit)
    (stop-server server)))

(use-fixtures :once once-fixture)

(defn e2e-selector [selector]
  (str "[data-e2e=" selector "]"))

(defn find-element [e2e-tag]
  (taxi/find-element {:css (e2e-selector e2e-tag)}))

(defn wait-for [e2e-tag]
  (taxi/wait-until #(find-element e2e-tag) 20000))

(defn click [e2e-tag]
  (taxi/click (e2e-selector e2e-tag)))

(defn fill-input [fill-map]
  (doseq [[k v] fill-map]
    (taxi/send-keys (e2e-selector k) v)))

(defn fill-past-date [e2e-tag]
  (click e2e-tag)
  (taxi/click (str (e2e-selector e2e-tag) " .datepicker.dp-dropdown .prev"))
  (taxi/click (str (e2e-selector e2e-tag) " .datepicker.dp-dropdown td:not(.old)")))

(defn text [e2e-tag]
  (taxi/text (e2e-selector e2e-tag)))

(deftest basic-journey-test
  (testing "go to home page/ conference list page"
    (taxi/to "http://localhost:4000/#")

    ; conference list page
    (wait-for "page-conference-list")
    (click "btn-add-conference")

    ; add conference page
    (wait-for "page-add-conference")
    (fill-input {"input-conference-series"      "some series"
                 "input-conference-name"        "some conference name"
                 "input-conference-link"        "www.some-link.org"
                 "input-conference-description" "some really fancy description with a new line. \nAnd here is the new line. Wohooo!"})
    (fill-past-date "date-conference-from")
    (fill-past-date "date-conference-to")
    (click "button-create-conference")

    ; conference detail page
    (wait-for "page-conference-detail")
    (is (= "SOME SERIES" (text "text-conference-series")))
    (is (= "some conference name" (text "text-conference-name")))
    (is (= "www.some-link.org" (text "text-conference-link")))))

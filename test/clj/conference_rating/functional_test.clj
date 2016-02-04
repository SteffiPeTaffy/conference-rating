(ns conference-rating.functional-test
  (:require [clj-webdriver.taxi :as taxi]
            [clj-webdriver.core :as core]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [conference-rating.server :as server]
            [clojure.string :as s]))

(taxi/set-driver! {:browser :chrome})


(defn start-server []
  (server/start-server 4000 false {}))

(defn stop-server [server]
  (.stop server))

(defn once-fixture [tests]
  (let [server (start-server)]
    (tests)
    (taxi/take-screenshot :file "screenshot-after-test.png")
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

(defn first-of-month [dates]
  (first (filter #(= "1" (core/text %)) dates)))

(defn fill-past-date [e2e-tag]
  (click e2e-tag)
  (taxi/click (str (e2e-selector e2e-tag) " .datepicker.dp-dropdown .prev"))
  (core/click (first-of-month (taxi/css-finder (str (e2e-selector e2e-tag) " .datepicker.dp-dropdown td")))))

(defn text [e2e-tag]
  (taxi/text (e2e-selector e2e-tag)))

(deftest basic-journey-test
  (testing "go to home page/ conference list page"
    (taxi/to "http://localhost:4000/#")
    (taxi/window-maximize)

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
    (is (= "some series" (s/lower-case (text "text-conference-series")))) ; different chromedrivers treat css transform differently
    (is (= "some conference name" (text "text-conference-name")))
    (is (not-empty (text "text-conference-from-to-dates")))
    (is (= "www.some-link.org" (text "text-conference-link")))
    (is (= "some really fancy description with a new line.\nAnd here is the new line. Wohooo!" (text "text-conference-description")))
    (click "button-add-new-rating")

    ; add rating page
    (wait-for "page-add-rating")
    (click "checkbox-rating-voice")
    (click "button-add-rating")

    ; conference detail page
    (wait-for "page-conference-detail")
    (is (= "1" (text "text-icon-panel-number")))))
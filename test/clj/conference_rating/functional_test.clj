(ns conference-rating.functional-test
  (:require [clj-webdriver.taxi :as taxi]
            [clj-webdriver.core :as core]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [conference-rating.server :as server]
            [clojure.string :as s])
  (:import (java.util UUID)))

(taxi/set-driver! {:browser :chrome})


(defn start-server []
  (server/start-server 4000 false {} nil true))

(defn stop-server [server]
  (.stop server))

(defn once-fixture [tests]
  (let [server (start-server)]
    (try
      (tests)
      (finally
        (taxi/take-screenshot :file "screenshot-after-test.png")
        (taxi/quit)
        (stop-server server)))))

(use-fixtures :once once-fixture)

(defn e2e-selector [selector]
  (str "[data-e2e=" selector "]"))

(defn find-element [e2e-tag]
  (taxi/find-element {:css (e2e-selector e2e-tag)}))

(defn wait-for [e2e-tag]
  (taxi/wait-until #(find-element e2e-tag) 60000))

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

(defn texts [e2e-tag]
  (map core/text (taxi/find-elements {:css (e2e-selector e2e-tag)})))

(defn not-contains? [x coll]
  (not (some #{x} coll)))

(deftest ^:functional basic-journey-test
  (let [conference-name (str "some conference name" (UUID/randomUUID))
        conference-series "some series"
        conference-link "www.some-link.org"
        conference-description "some really fancy description with a new line.\nAnd here is the new line. Wohooo!"]

    ; open conference voices home page
    (testing "go to home page/ conference list page"
      (taxi/to "http://localhost:4000/#")
      (taxi/window-maximize)

      ; shows a list os conferences
      (wait-for "page-conference-list")
      (click "btn-add-conference")

      ; adds conference
      (wait-for "page-add-conference")
      (fill-input {"input-conference-series"      conference-series
                   "input-conference-name"        conference-name
                   "input-conference-link"        conference-link
                   "input-conference-description" conference-description})
      (fill-past-date "date-conference-from")
      (fill-past-date "date-conference-to")
      (click "button-create-conference")

      ; detail page of the newly added conference
      (wait-for "page-conference-detail")
      (is (= conference-series (s/lower-case (text "text-conference-series")))) ; different chromedrivers treat css transform differently
      (is (= conference-name (text "text-conference-name")))
      (is (not-empty (text "text-conference-from-to-dates")))
      (is (= conference-link (text "text-conference-link")))
      (is (= conference-description (text "text-conference-description")))
      (click "button-add-new-rating")

      ; add rating to newly created conference
      (wait-for "page-add-rating")
      (click "checkbox-rating-voice")
      (click "button-add-rating")

      ; checks that the rating is added and visible on the detail page
      (wait-for "page-conference-detail")
      (taxi/wait-until #(= "1" (text "text-icon-panel-number")))

      ; deletes conference
      (click "button-delete-conference")
      (taxi/accept)

      ; back to the conference list page
      (wait-for "page-conference-list")
      (is (not-contains? conference-name (texts "text-conference-name"))))))


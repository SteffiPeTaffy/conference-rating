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
  (taxi/wait-until #(find-element e2e-tag) 10000))

(defn click [e2e-tag]
  (taxi/click (e2e-selector e2e-tag)))

(defn fill-input [fill-map]
  (doseq [[k v] fill-map]
    (taxi/clear (e2e-selector k))
    (taxi/send-keys (e2e-selector k) v)))

(defn first-of-month [dates]
  (first (filter #(= "1" (core/text %)) dates)))

(defn fill-date [e2e-tag type]
  (click e2e-tag)
  (taxi/click (str (e2e-selector e2e-tag) " .datepicker.dp-dropdown " type))
  (core/click (first-of-month (taxi/css-finder (str (e2e-selector e2e-tag) " .datepicker.dp-dropdown td")))))

(defn fill-past-date [e2e-tag]
  (fill-date e2e-tag ".prev"))

(defn fill-future-date [e2e-tag]
  (fill-date e2e-tag ".next"))

(defn text [e2e-tag]
  (taxi/text (e2e-selector e2e-tag)))

(defn texts [e2e-tag]
  (map core/text (taxi/find-elements {:css (e2e-selector e2e-tag)})))

(defn not-contains? [x coll]
  (not (some #{x} coll)))

(defn navigate-to-conference-by-url [conference-url]
  (taxi/to conference-url)
  (wait-for "page-conference-detail"))

(deftest ^:functional basic-journey-test
  (let [conference-series (str "some unique series" (UUID/randomUUID))
        past-conference-name (str "some unique conference name" (UUID/randomUUID))
        past-conference-link "www.some-link.org"
        past-conference-description "some really fancy description with a new line.\nAnd here is the new line. Wohooo!"
        future-conference-name (str "some other unique conference name" (UUID/randomUUID))]

    ; open conference voices home page
    (testing "go to home page/ conference list page"
      (taxi/to "http://localhost:4000/#")
      (taxi/window-maximize)

      ; shows a list os conferences
      (wait-for "page-conference-list")
      (click "btn-add-conference")

      ; adds past conference
      (wait-for "page-add-conference")
      (fill-input {"input-conference-series"      conference-series
                   "input-conference-name"        past-conference-name
                   "input-conference-link"        past-conference-link
                   "input-conference-description" past-conference-description})
      (fill-past-date "date-conference-from")
      (fill-past-date "date-conference-to")
      (click "button-create-conference")

      ; detail page of the newly added past conference
      (wait-for "page-conference-detail")
      (let [past-conference-url (taxi/current-url)]
        (is (= conference-series (s/lower-case (text "text-conference-series")))) ; different chromedrivers treat css transform differently
        (is (= past-conference-name (text "text-conference-name")))
        (is (not-empty (text "text-conference-from-to-dates")))
        (is (= past-conference-link (text "text-conference-link")))
        (is (= past-conference-description (text "text-conference-description")))
        (is (= "This conference has not been rated yet. Be the first one to give it your voice!" (text "no-ratings-info")))

        ; adds future conference series
        (click "btn-add-conference")
        (wait-for "page-add-conference")
        (fill-input {"input-conference-series"      conference-series
                     "input-conference-name"        future-conference-name
                     "input-conference-link"        "www.some-other-link.org"
                     "input-conference-description" "some other description."})
        (fill-future-date "date-conference-from")
        (fill-future-date "date-conference-to")
        (click "button-create-conference")

        ; detail page of the newly added future conference
        (wait-for "page-conference-detail")
        (let [future-conference-url (taxi/current-url)]
          (is (= conference-series (s/lower-case (text "text-conference-series")))) ; different chromedrivers treat css transform differently
          (is (= future-conference-name (s/lower-case (text "text-conference-name"))))
          (is (= "This conference has not started yet and no conference of this series has been rated yet. Come back later!" (text "no-ratings-info")))

          ; navigate to newly created past conference
          (navigate-to-conference-by-url past-conference-url)

          ; add rating to newly created past conference
          (click "button-add-new-rating")
          (wait-for "page-add-rating")
          (click "checkbox-rating-voice")
          (click "button-add-rating")

          ; checks that the rating is added and visible on the detail page
          (wait-for "page-conference-detail")
          (taxi/wait-until #(= "1" (text "text-icon-panel-number")))

          ; navigates to the future conference of the series
          (navigate-to-conference-by-url future-conference-url)

          ; checks that the rating is also visible to the future conference of the same series
          (is (= conference-series (s/lower-case (text "text-conference-series")))) ; different chromedrivers treat css transform differently
          (is (= future-conference-name (s/lower-case (text "text-conference-name"))))
          (is (= (str "Note: This ratings are aggregated from ratings of previous " conference-series " conferences.") (text "aggregated-ratings-info")))

          ; navigate to newly created past conference
          (navigate-to-conference-by-url past-conference-url)

          ; edits past conference
          (click "button-edit-conference")
          (wait-for "page-add-conference")
          (fill-input {"input-conference-description" "some edited description"})
          (click "button-create-conference")

          ; detail page of the edited past conference
          (wait-for "page-conference-detail")
          (is (= "some edited description" (text "text-conference-description")))

          ; deletes past conference
          (click "button-delete-conference")
          (taxi/accept)

          ; back to the conference list page and checks that conference is gone
          (wait-for "page-conference-list")
          (is (not-contains? past-conference-name (texts "text-conference-name")))

          ; searches for and navigates to the future conference of the series
          (navigate-to-conference-by-url future-conference-url)

          ; checks that future conference does not have aggregated ratings anymore
          (is (= conference-series (s/lower-case (text "text-conference-series")))) ; different chromedrivers treat css transform differently
          (is (= future-conference-name (s/lower-case (text "text-conference-name"))))
          (is (= "This conference has not started yet and no conference of this series has been rated yet. Come back later!" (text "no-ratings-info"))))))))


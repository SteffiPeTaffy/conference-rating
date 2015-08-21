(ns conference-rating.home.home
  (:require [reagent.core :refer [atom]]
            [conference-rating.rating.rating  :as rating]
            [conference-rating.conference.conference :as conference]
            [conference-rating.conference.conference-list :as conference-list]))

(defonce displayed-conference (atom nil))
(defonce conferences (atom nil))
(defonce ratings (atom nil))

;; -------------------------
;; Views

(defn display-loading []
  [:div [:h2 "Loading..."]])

(defn display-conference [conference]
  [:div {:class "container"}
   (conference/display-conference-overview conference)])

(defn conference-page []
  (let [conference @displayed-conference]
    (if (not (nil? conference))
      (display-conference conference)
      (display-loading))))

(defn conferences-page []
  (let [conference-list @conferences]
    (if (not (nil? conference-list))
      (conference-list/display-conference-list conference-list)
      (display-loading))))
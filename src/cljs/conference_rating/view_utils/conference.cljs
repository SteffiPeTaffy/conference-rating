(ns conference-rating.view-utils.conference
  (:require [conference-rating.util :as util]
            [cljs-time.core :as t]
            [conference-rating.backend :as backend]))

(defn- is-future-conference? [conference]
  (t/after? (util/parse-string-to-date (:from conference)) (t/now)))

(defn- ratings-key-for [conference]
  (if (is-future-conference? conference)
    :average-series-rating
    :aggregated-ratings))

(defn- is-identical-user? [user]
  (= (:email user) (:email @conference-rating.user-info/user-info)))

(defn- is-attending? [conference]
  (some is-identical-user? (:attendees conference)))

(defn- number-of-attendees [conerence]
  (count (:attendees conerence)))


(defn add-rating-button [conference styles label]
  (if (not (is-future-conference? conference))
    [:div {:class "text-lg-right"}
     [:a {:class (str "btn btn-orange voice-btn " styles) :href (str "#/conferences/" (:_id conference) "/add-rating")}
      [:span {:class "glyphicon glyphicon-bullhorn hidden-sm"}]
      label]]))

(defn attending-button [conference styles reload-attendance-fn]
  (let [attending-btn-label
        (if (is-future-conference? conference)
          "I am going"
          "I was here")]
    (if (not (is-attending? conference))
      [:div {:class "text-lg-right"}
       [:a {:class (str "btn btn-attending " styles) :on-click #(backend/attend-conference (:_id conference) reload-attendance-fn)}
        [:span {:class "glyphicon glyphicon-map-marker"}]
        attending-btn-label]])))

(defn attending-label [conference]
  (let [number-of-attendees (number-of-attendees conference)]
    (if (is-future-conference? conference)
      (if (= 0 number-of-attendees)
        "No one is going, yet."
        (str "You and " (- 1 number-of-attendees) " others are going."))
      (if (= 0 number-of-attendees)
        "No one was here."
        (str "You and " (- 1 number-of-attendees) " others were here.")))))

(defn attending-summary-label [conference]
    [:div
     [:p (attending-label conference)]])

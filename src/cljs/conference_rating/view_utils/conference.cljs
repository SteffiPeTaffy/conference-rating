(ns conference-rating.view-utils.conference
  (:require [conference-rating.util :as util]
            [cljs-time.core :as t]
            [conference-rating.backend :as backend]
            [clojure.string :as string]))

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

(defn- has-already-voted? [conference]
  (some is-identical-user? (:voters conference)))

(defn- attendee-label [user]
  [:div
   [:a {:href (str "mailto:" (:email user))} (str (:email user) )]])

(defn- conference-attendees-tooltip [conference]
  (string/join "\n" (map :email (:attendees  conference))))

(defn conference-voicers-label [conference]
  (string/join "\n" (map :email (:voters conference))))

(defn add-voice-button [conference styles label]
  (if (not (is-future-conference? conference))
    (if (has-already-voted? conference)
      [:div {:class "text-lg-right"}
       [:button {:class (str "btn btn-orange voice-btn " styles) :disabled "disabled" :title "You already voted." :data-e2e "button-already-voted"}
        [:span {:class "glyphicon glyphicon-bullhorn hidden-sm"}]
        label]]
      [:div {:title (conference-voicers-label conference) :class "text-lg-right"}
       [:a {:class (str "btn btn-orange voice-btn " styles) :data-e2e "button-add-new-rating" :href (str "#/conferences/" (:_id conference) "/add-rating")}
        [:span {:class "glyphicon glyphicon-bullhorn hidden-sm"}]
        label]])))

(defn attending-button [conference styles reload-attendance-fn]
  (if (is-attending? conference)
    [:div
     [:a {:class (str "btn btn-attending " styles) :title "unattend conference" :data-e2e "button-unattend-conference" :on-click #(backend/unattend-conference (:_id conference) reload-attendance-fn)}
      [:span {:class "glyphicon glyphicon-map-marker"}]
      "undo"]]
    (let [attending-btn-label
          (if (is-future-conference? conference)
            "I am going"
            "I was here")]
      [:div
       [:a {:class (str "btn btn-attending " styles) :data-e2e "button-attend-conference" :on-click #(backend/attend-conference (:_id conference) reload-attendance-fn)}
        [:span {:class "glyphicon glyphicon-map-marker"}]
        attending-btn-label]])))

(defn attending-label [conference]
  (let [number-of-attendees (number-of-attendees conference)
        is-attending (is-attending? conference)]
    (if (is-future-conference? conference)
      (if (= 0 number-of-attendees)
        "No one is going, yet."
        (if is-attending
          (str "You and " (- number-of-attendees 1) " others are going.")
          (str number-of-attendees " others are going.")))
      (if (= 0 number-of-attendees)
        "No one was here."
        (if is-attending
          (str "You and " (- number-of-attendees 1) " others were here.")
          (str number-of-attendees " others were here."))))))

(defn attending-summary-label [conference]
    [:div
     [:p {:title (conference-attendees-tooltip conference) :data-e2e "text-attendees"} (attending-label conference)]])

(defn attendees-list-label [conference]
  [:div (map attendee-label (:attendees conference))])

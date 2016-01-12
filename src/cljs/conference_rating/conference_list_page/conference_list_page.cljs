(ns conference-rating.conference-list-page.conference-list-page
  (:require [reagent.core :as reagent :refer [atom]]
            [conference-rating.view-utils.navbar :as navbar]
            [conference-rating.conference-list-page.conference-list-entry :as list-entry]
            [conference-rating.util :as util]
            [cljs-time.core :as t]
            [conference-rating.history :as history]
            [conference-rating.view-utils.typeahead :as typeahead]))


(defn search-for-conference-input []
  [:input {:type "text" :class "form-control search-for-conference-input" :placeholder "search for conference"}])


(defn matching [name q]
  (let [name-lower-case (.toLowerCase name)
        input-lower-case (.toLowerCase q)
        index-of-result (.indexOf name-lower-case input-lower-case)
        not-result (not= index-of-result -1)]
    not-result))

(defn conference-suggestion-template [conference]
  (let [series (:series conference)
        name (:name conference)
        from-date (util/format-date (:from conference))
        to-date (util/format-date (:to conference))]
    (str "<div class=\"conference-suggestion-template\">"
         "<p>"series"</p>"
         "<h4>"name"</h4>"
         "<p>"from-date " - " to-date "</p>"
         "</div>")))


(defn go-to-conference [conference]
  (history/redirect-to (str "/conferences/" (:_id conference))))


(defn conference-suggestion-source [conference-list]
  (fn [q cb] (let [match (->> conference-list
                              (filter #(not (nil? (:name %))))
                              (filter #(matching (:name %) q)))]
               (cb  match))))

(defn conference-name [conference] (:name conference))

(defn search-for-conference-component [conference-list]
  (typeahead/init-typeahead
    search-for-conference-input
    (typeahead/config {:hint true,
                       :highlight true,
                       :minLength 1})
    (typeahead/data-sets {:name "conferences",
                          :source (conference-suggestion-source conference-list)
                          :display conference-name
                          :templates {:suggestion conference-suggestion-template}})
    #(go-to-conference %2)))


(defn add-conference-bar []
  [:div {:class "add-conference-btn-container"}
   [:a {:class "btn btn-md btn-orange mar-bottom" :href "#/add-conference"} "new conference"]])

(defn- upcoming-conference? [conference]
  (t/after? (util/parse-date (:to conference)) (t/now)))

(defn- past-conference? [conference]
  (not (upcoming-conference? conference)))

(defn display-conference-list [conference-list]
  (let [upcoming-conferences (filter upcoming-conference? conference-list)
        past-conferences (filter past-conference? conference-list)]
    [:div
     (navbar/nav-bar)
     [:div {:class "container-fluid content-container pad-top"}
      [:div {:class "conference-search form-group"}
       [(search-for-conference-component conference-list)]]
      (add-conference-bar)
      [:h3 "Upcoming conferences"]
      [:div {:class "row"} (map list-entry/display-conference-list-item (sort-by :to upcoming-conferences))]
      [:h3 "Past conferences"]
      [:div {:class "row"} (map list-entry/display-conference-list-item (reverse (sort-by :to past-conferences)))]]]))

(defonce displayed-conferences (atom nil))

(defn conferences-page []
  (let [conference-list @displayed-conferences]
    (if (not (nil? conference-list))
      (display-conference-list conference-list)
      (util/display-loading))))
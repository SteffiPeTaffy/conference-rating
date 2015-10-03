(ns conference-rating.conference-list-page.conference-list-page
  (:require [reagent.core :as reagent :refer [atom]]
            [conference-rating.view-utils.header :as header]
            [conference-rating.conference-list-page.conference-list-entry :as list-entry]
            [conference-rating.util :as util]
            [conference-rating.history :as history]))


(defn search-for-conference-input []
  [:input {:type "text" :class "form-control search-for-conference-input" :placeholder "search for conference"}])

(def typeaheadConfig
  (clj->js {
            :hint true,
            :highlight true,
            :minLength 1
            }))

(defn matching [name q]
  (let [name-lower-case (.toLowerCase name)
        input-lower-case (.toLowerCase q)
        index-of-result (.indexOf name-lower-case input-lower-case)
        not-result (not= index-of-result -1)]
    not-result))

(defn conference-suggestion-template [conference]
  (let [conference (js->clj conference :keywordize-keys true)
        series (:series conference)
        name (:name conference)
        from-date (util/format-date (:from conference))
        to-date (util/format-date (:to conference))]
    (str "<div class=\"conference-suggestion-template\">"
         "<p>"series"</p>"
         "<h4>"name"</h4>"
         "<p>"from-date " - " to-date "</p>"
         "</div>")))

(defn typeaheadDataConfig [conference-list]
  (clj->js {
            :name "conferences",
            :source (fn [q cb] (let [match (->> conference-list
                                                (filter (fn [conference] (not (nil? (:name conference)))))
                                                (filter (fn [conference] (matching (:name conference) q))))]
                                 (cb (clj->js match))))
            :display (fn [conference] (:name (js->clj conference :keywordize-keys true)))
            :templates {:suggestion conference-suggestion-template}}))

(defn go-to-conference [conference]
  (history/redirect-to (str "/conferences/" (:_id conference))))

(defn search-for-conference-component [conference-list]
  (with-meta search-for-conference-input
             {:component-did-mount
              (fn [this]
                (doto (js/jQuery (reagent/dom-node this))
                  (.typeahead  typeaheadConfig (typeaheadDataConfig conference-list))
                  (.bind "typeahead:select" #(go-to-conference (js->clj %2 :keywordize-keys true)))))}))

(defn display-conference-list [conference-list]
  [:div
   (header/nav-bar)
   [:div {:class "container-fluid content-container pad-top"}
    [:div {:class "conference-search form-group"}
     [(search-for-conference-component conference-list)]]
    (header/add-conference-bar)
    [:div {:class "row"}(map list-entry/display-conference-list-item (sort-by :from conference-list))]]])

(defonce displayed-conferences (atom nil))

(defn conferences-page []
  (let [conference-list @displayed-conferences]
    (if (not (nil? conference-list))
      (display-conference-list conference-list)
      (util/display-loading))))
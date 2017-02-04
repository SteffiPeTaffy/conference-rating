(ns conference-rating.view-utils.navbar
  (:require [conference-rating.view-utils.typeahead :as typeahead]
            [conference-rating.history :as history]
            [conference-rating.util :as util]
            [conference-rating.user-info :as user-info]))

(defn search-for-conference-input []
  [:input {:type "text"
           :class "form-control search-for-conference-input"
           :placeholder "Search for conference"
           :data-e2e "conference-search-input"
           :style {:background "transparent"
                   :color "white"
                   :border-color "transparent"}}])

(defn conference-suggestion-template [conference]
  (let [series (:series conference)
        name (:name conference)
        from-date (util/format-to-human-readable-date (:from conference))
        to-date (util/format-to-human-readable-date (:to conference))]
    (str "<div class=\"conference-suggestion-template\" data-e2e=\"conference-suggestion-template\">"
         "<p>" series "</p>"
         "<h4>" name "</h4>"
         "<p>" from-date " - " to-date "</p>"
         "</div>")))

(defn go-to-conference [conference]
  (history/redirect-to (str "/conferences/" (:_id conference))))


(defn matching [name q]
  (let [name-lower-case (.toLowerCase name)
        input-lower-case (.toLowerCase q)
        index-of-result (.indexOf name-lower-case input-lower-case)
        not-result (not= index-of-result -1)]
    not-result))

(defn conference-suggestion-source [conference-list]
  (fn [q cb] (let [match (->> conference-list
                              (filter #(not (nil? (:name %))))
                              (filter #(matching (:name %) q)))]
               (cb match))))

(defn conference-name [conference] (:name conference))

(defn search-for-conference-component [conference-list]
  (typeahead/init-typeahead
    search-for-conference-input
    (typeahead/config {:hint      true,
                       :highlight true,
                       :minLength 1})
    (typeahead/data-sets {:name      "conferences",
                          :source    (conference-suggestion-source conference-list)
                          :display   conference-name
                          :templates {:suggestion conference-suggestion-template}})
    #(go-to-conference %2)))

(defn user-info-text [user-info]
  (if (and (:firstName user-info)
           (:lastName user-info))
    (str "Hey " (:firstName user-info) " " (:lastName user-info) "!")
    (str "Hey " (:email user-info) "!")))

(defn nav-bar [conference-list]
  [:nav {:class "navbar navbar-inverse navbar-fixed-top"}
   [:div {:class "container-fluid"}
    [:div {:class "navbar-header"}
     [:a {:class "navbar-brand" :href "#" :data-e2e "conference-voices-brand"}
      [:span {:class "cl-yellow"} "conference"]
      [:span " voices"]
      [:span {:class "glyphicon glyphicon-bullhorn"}]
      [:span {:class "navbar-user-info"} (user-info-text @user-info/user-info)]]]
    [:div {:class "conference-search form-group text-lg-right"}
     [:div {:class "action-bar-container"}
      [(search-for-conference-component conference-list)]
      [:a {:class "btn btn-sm btn-orange mar-bottom button-add-conference" :href "#/add-conference" :data-e2e "btn-add-conference"} "Add conference"]]]]])

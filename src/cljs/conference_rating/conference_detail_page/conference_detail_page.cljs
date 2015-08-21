(ns conference-rating.conference-detail-page.conference-detail-page
  (:require [conference-rating.view-utils.panel :as panel]
            [reagent.core :as reagent :refer [atom]]
            [conference-rating.view-utils.header :as header]
            [conference-rating.conference-detail-page.rating-list :as rating-list]
            [conference-rating.util :as util]))

(def list-of-ratings
  [{:recommended true
    :roles       [:dev :devops :other]
    :experience  [:rookie :beginner :intermediate]
    :tags [:inspiring :informative]
    :overall 4
    :talks 4
    :venue 3
    :networking 2
    :comment     {:name    "Constantin Bader"
                  :comment "This conference was super awesome!"}}
   {:recommended true
    :roles       [:dev :devops]
    :experience  [:intermediate :advanced]
    :tags [:inspiring :informative :entertaining]
    :overall 3
    :talks 4
    :venue 2
    :networking 1
    :comment     {:name    "Florian Sellmayr"
                  :comment "This conference was mehh and awesome at the same time!"}}])

(def aggregated-ratings
  {:aggregated-ratings {:number-of-ratings 16
                        :recommendations 1445
                        :overall {:avg 4 :count 8}
                        :talks {:avg 0.1 :count 7}
                        :venue {:avg 3 :count 6}
                        :community {:avg 2.5 :count 5}
                        :roles {
                                :dev 11
                                :devops 2
                                :ux 1
                                :qa 0
                                :ba 0
                                :pm 0
                                :sales 0
                                :recruting 1
                                :other 1}
                        :experience {
                                     :rookie 1
                                     :beginner 3
                                     :intermediate 12
                                     :advanced 5
                                     :expert 0}
                        :tags {
                               :inspiring 2
                               :entertaining 2
                               :learning 2
                               :potential-hires 1
                               :potential-clients 1}}})


(defn badge [label classes]
  [:span {:class (str "badge " classes)} label])

(defn conference-badges-row [& badges]
  [:div {:class "badge-row"} badges ])

(defn conference-dates [from-date to-date]
  (cond
    (and from-date from-date) [:p {:class "conference-dates"} (str from-date " - " to-date)]
    from-date [:p {:class "conference-dates"} from-date]
    :else [:p {:class "conference-dates"} "TBD"]))

(defn series-tag [series-tag]
  (if (not (nil? series-tag))
    [:div {:class "series-tag-container"}[:span {:class "series-tag"} series-tag]]))

(defn link [link]
  [:p {:class "conference-link"}[:a {:href link :class "conference-link"} link]])

(defn conference-information [conference]
  [:div {:class "row conference-information-container bg-light cl-dark"}
    (series-tag (:series conference))
    [:h1(:name conference)]
    (conference-dates (:from conference) (:to conference))
    [:h4 (:description conference)]
    (link (:link conference))])

(defn conference-recommendations [numberOfRecommendations]
   (panel/icon-panel "glyphicon-star" numberOfRecommendations "would go again" "bg-yellow cl-light"))

;;TODO show sentence for experience level, too
(defn average-attendee []
  [:div
   [:p "I am a Dev or QA"]
   [:p "I am looking for entertainment and fun"]
   [:p "I want to learn a lot"]
   [:p "I want to eatch out for potential clients"]
   [:p "I want to watch out for potential hires"]])

;;TODO show badges for experience level (in navy lue), too

(defn conference-badges [simple-conference]
  (let [dev-count (get-in simple-conference [:aggregated-ratings :roles :dev])]
  [:div
   (conference-badges-row (if (> dev-count 0) [badge (str "DEV " dev-count) "badge-light-blue"])
                          (badge "QA" "badge-light-blue")
                          (badge "BA" "badge-light-blue"))
   (conference-badges-row (badge "Inspiring" "badge-light-primary")
                          (badge "learnings" "badge-light-primary")
                          (badge "Network" "badge-light-primary")
                          (badge "Hires" "badge-light-primary")
                          (badge "Clients" "badge-light-primary"))]))

(defn conference-average-attendee [simple-conference]
  [:div
   (panel/info-panel "glyphicon-user" "I am your average attende" (average-attendee) (conference-badges simple-conference))])

(defn add-rating-button [conference-id]
  [:div {:class "text-lg-right voice-btn-container"}
   [:a {:class "btn btn-md btn-orange" :href (str "#/conferences/" conference-id "/add-rating")} "give it your voice"]])

(defn display-conference-overview [simple-conference]
  (let [conference (merge simple-conference aggregated-ratings)
        overall-avg (get-in conference [:aggregated-ratings :overall :avg])
        overall-avg-percentage (* (/ overall-avg 4) 100)
        overall-ratings-str (str (get-in conference [:aggregated-ratings :overall :count]) " ratings")
        talks-avg (get-in conference [:aggregated-ratings :talks :avg])
        talks-avg-percentage (* (/ talks-avg 4) 100)
        talks-ratings-str (str (get-in conference [:aggregated-ratings :talks :count]) " ratings")
        venue-avg (get-in conference [:aggregated-ratings :venue :avg])
        venue-avg-percentage (* (/ venue-avg 4) 100)
        venue-ratings-str (str (get-in conference [:aggregated-ratings :venue :count]) " ratings")
        networking-avg (get-in conference [:aggregated-ratings :community :avg])
        networking-avg-percentage (* (/ networking-avg 4) 100)
        networking-ratings-str (str (get-in conference [:aggregated-ratings :community :count]) " ratings")]
    [:div
   (header/nav-bar)
   [:div {:class "container-fluid content-container pad-top conference-container"}
    [:div {:class "row"}
     [:div {:class "col-lg-8 col-md-8"}
      (conference-information conference)
      (add-rating-button (:_id conference))]
     [:div {:class "col-lg-4 col-md-4 aggregated-ratings-container"}
      (conference-recommendations (get-in conference [:aggregated-ratings :recommendations]))
      [:div {:class "row"}
       [:div {:class "col-lg-6 col-md-6 col-sm-6"}
        [:div (panel/range-panel overall-avg-percentage overall-avg "Overall" overall-ratings-str "bg-mint" "glyphicon-thumbs-up")]
        [:div (panel/range-panel talks-avg-percentage talks-avg "Talks" talks-ratings-str "bg-purple" "glyphicon-user")]]
       [:div {:class "col-lg-6 col-md-6 col-sm-6"}
        [:div (panel/range-panel venue-avg-percentage venue-avg "Venue" venue-ratings-str "bg-pink" "glyphicon-home")]
        [:div (panel/range-panel networking-avg-percentage networking-avg "Networking" networking-ratings-str "bg-green" "glyphicon-glass")]]]
      (conference-average-attendee conference)]]
    [:div {:class "row"}
     [:div {:class "col-lg-12 col-md-12"}
      (rating-list/display-rating-list list-of-ratings)]]]]))

(defonce displayed-conference (atom nil))
(defonce ratings (atom nil))

(defn- display-conference [conference]
  [:div {:class "container"}
   (display-conference-overview conference)])

(defn conference-page []
  (let [conference @displayed-conference]
    (if (not (nil? conference))
      (display-conference conference)
      (util/display-loading))))
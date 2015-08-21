(ns conference-rating.conference.conference
  (:require [conference-rating.panel :as panel]
            [conference-rating.header :as header]))



(def single-rating
  {:recommended true
   :roles       [:dev :devops :qa :ux :pm :ba :sales :recruiting :other]
   :experience  [:rookie :beginner :intermediate :advanced :expert]
   :tags [:inspiring :informative :entertaining :potential-hires :potential-clients]
   :overall 4
   :talks 4
   :venue 3
   :networking 2
   :comment     {:name    "Constantin Bader"
                 :comment "This conference was super awesome!"}})

(def aggregated-ratings
  {:aggregated-ratings {:number-of-ratings 16
                        :recommendations 14
                        :overall {:avg 4 :count 2}
                        :talks {:avg 4 :count 2}
                        :venue {:avg 2.5 :count 2}
                        :community {:avg 4.5 :count 2}
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
  [:div {:class "badge-row"} badges])

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
(defn conference-badges []
  [:div
   (conference-badges-row (badge "DEV" "badge-light-blue")
                          (badge "QA" "badge-light-blue")
                          (badge "BA" "badge-light-blue"))
   (conference-badges-row (badge "Inspiring" "badge-light-primary")
                          (badge "learnings" "badge-light-primary")
                          (badge "Network" "badge-light-primary")
                          (badge "Hires" "badge-light-primary")
                          (badge "Clients" "badge-light-primary"))])

(defn conference-average-attendee []
  [:div
   (panel/info-panel "glyphicon-user" "I am your average attende" (average-attendee) (conference-badges))])

(defn add-rating-button [conference-id]
  [:div {:class "text-lg-right voice-btn-container"}
   [:a {:class "btn btn-md btn-orange" :href (str "#/conferences/" conference-id "/add-rating")} "give it your voice"]])

(defn display-conference-overview [simple-conference]
  (let [conference (merge simple-conference aggregated-ratings)]
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
        [:div (panel/range-panel 83 83 "Overall" "12 ratings" "bg-mint" "glyphicon-thumbs-up")]
        [:div (panel/range-panel 97 83 "Talks" "5 ratings" "bg-purple" "glyphicon-user")]]
       [:div {:class "col-lg-6 col-md-6 col-sm-6"}
        [:div (panel/range-panel 67 83 "Venue" "3 ratings" "bg-pink" "glyphicon-home")]
        [:div (panel/range-panel 35 83 "Networking" "5 ratings" "bg-green" "glyphicon-glass")]]]
      (conference-average-attendee)]]]]))



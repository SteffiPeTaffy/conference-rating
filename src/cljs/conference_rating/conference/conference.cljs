(ns conference-rating.conference.conference
  (:require [conference-rating.panel :as panel]
            [conference-rating.header :as header]))

(defn badge [label classes]
  [:span {:class (str "badge " classes)} label])

(defn conference-badges-row [& badges]
  [:div {:class "badge-row"} badges])

(defn conference-information [conference]
  [:div {:class "row conference-information-container bg-light cl-dark"}
    [:h1(:name conference)]
    [:p "15 September 2015"]
    [:h3 (:description conference)]])

(defn conference-recommendations []
   (panel/icon-panel "glyphicon-star" 17 "would go again" "bg-yellow cl-light"))

(defn average-attendee []
  [:div
   [:p "I am a Dev or QA"]
   [:p "I am looking for entertainment and fun"]
   [:p "I want to learn a lot"]
   [:p "I want to eatch out for potential clients"]
   [:p "I want to watch out for potential hires"]])

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
  [:div {:class "text-lg-right"}
   [:a {:class "btn btn-md btn-orange voice-btn" :href (str "#/conferences/" conference-id "/add-rating")} "give it your voice"]])

(defn display-conference-overview [conference]
  [:div
   (header/nav-bar)
   [:div {:class "container-fluid content-container pad-top"}
    [:div {:class "row"}
     [:div {:class "col-lg-8"}
      (conference-information conference)
      (add-rating-button (:_id conference))]
     [:div {:class "col-lg-4"}
      (conference-recommendations)
      [:div {:class "row"}
       [:div {:class "col-sm-6 col-lg-6"}
        [:div (panel/range-panel 83 83 "Overall" "12 ratings" "bg-mint" "glyphicon-thumbs-up")]
        [:div (panel/range-panel 97 83 "Talks" "5 ratings" "bg-purple" "glyphicon-user")]]
       [:div {:class "col-sm-6 col-lg-6"}
        [:div (panel/range-panel 67 83 "Venue" "3 ratings" "bg-pink" "glyphicon-home")]
        [:div (panel/range-panel 35 83 "Networking" "5 ratings" "bg-green" "glyphicon-glass")]]]
      (conference-average-attendee)]]]])



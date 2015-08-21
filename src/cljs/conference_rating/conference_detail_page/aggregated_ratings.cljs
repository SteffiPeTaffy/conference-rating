(ns conference-rating.conference-detail-page.aggregated-ratings
  (:require [conference-rating.view-utils.panel :as panel]))

(defn conference-recommendations [numberOfRecommendations]
  (panel/icon-panel "glyphicon-star" numberOfRecommendations "would go again" "bg-yellow cl-light"))

(defn badge [label classes]
  [:span {:class (str "badge " classes)} label])

(defn conference-badges-row [& badges]
  [:div {:class "badge-row"} badges ])

;;TODO show sentence for experience level, too
(defn average-attendee []
  [:div
   [:p "I am a Dev or QA"]
   [:p "I am looking for entertainment and fun"]
   [:p "I want to learn a lot"]
   [:p "I want to eatch out for potential clients"]
   [:p "I want to watch out for potential hires"]])

(defn- selection-of-role-elements [aggregated-ratings role label]
  (let [count (role aggregated-ratings)]
    (if (> count 0) [badge (str label " " count) "badge-light-blue"])))

(defn conference-badges [aggregated-ratings]
  [:div
   (conference-badges-row (selection-of-role-elements aggregated-ratings :dev "DEV")
                          (selection-of-role-elements aggregated-ratings :devops "DEVOPS")
                          (selection-of-role-elements aggregated-ratings :ux "UX")
                          (selection-of-role-elements aggregated-ratings :qa "QA")
                          (selection-of-role-elements aggregated-ratings :ba "BA")
                          (selection-of-role-elements aggregated-ratings :pm "PM")
                          (selection-of-role-elements aggregated-ratings :sales "Sales")
                          (selection-of-role-elements aggregated-ratings :recruting "Recruting")
                          (selection-of-role-elements aggregated-ratings :other "Others"))
   (conference-badges-row (badge "Inspiring" "badge-light-primary")
                          (badge "learnings" "badge-light-primary")
                          (badge "Network" "badge-light-primary")
                          (badge "Hires" "badge-light-primary")
                          (badge "Clients" "badge-light-primary"))])

(defn conference-average-attendee [aggregated-ratings]
  [:div
   (panel/info-panel "glyphicon-user" "I am your average attende" (average-attendee) (conference-badges aggregated-ratings))])


(defn display-aggregated-ratings [aggregated-ratings]
  (let  [overall-avg (get-in aggregated-ratings [:overall :avg])
        overall-avg-percentage (* (/ overall-avg 4) 100)
        overall-ratings-str (str (get-in aggregated-ratings [:overall :count]) " ratings")
        talks-avg (get-in aggregated-ratings [:talks :avg])
        talks-avg-percentage (* (/ talks-avg 4) 100)
        talks-ratings-str (str (get-in aggregated-ratings [:talks :count]) " ratings")
        venue-avg (get-in aggregated-ratings [:venue :avg])
        venue-avg-percentage (* (/ venue-avg 4) 100)
        venue-ratings-str (str (get-in aggregated-ratings [:venue :count]) " ratings")
        networking-avg (get-in aggregated-ratings [:community :avg])
        networking-avg-percentage (* (/ networking-avg 4) 100)
        networking-ratings-str (str (get-in aggregated-ratings [:community :count]) " ratings")]
  [:div
   (conference-recommendations (:recommendations aggregated-ratings))
   [:div {:class "row"}
    [:div {:class "col-lg-6 col-md-6 col-sm-6"}
     [:div (panel/range-panel overall-avg-percentage overall-avg "Overall" overall-ratings-str "bg-mint" "glyphicon-thumbs-up")]
     [:div (panel/range-panel talks-avg-percentage talks-avg "Talks" talks-ratings-str "bg-purple" "glyphicon-user")]]
    [:div {:class "col-lg-6 col-md-6 col-sm-6"}
     [:div (panel/range-panel venue-avg-percentage venue-avg "Venue" venue-ratings-str "bg-pink" "glyphicon-home")]
     [:div (panel/range-panel networking-avg-percentage networking-avg "Networking" networking-ratings-str "bg-green" "glyphicon-glass")]]]
   (conference-average-attendee aggregated-ratings)]))



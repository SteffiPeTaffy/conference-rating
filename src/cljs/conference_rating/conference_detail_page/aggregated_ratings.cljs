(ns conference-rating.conference-detail-page.aggregated-ratings
  (:require [conference-rating.view-utils.panel :as panel]
            [conference-rating.util :as util]
            ))

(defn conference-recommendations [numberOfRecommendations]
  (panel/icon-panel "glyphicon-star" numberOfRecommendations "would go again" "bg-yellow cl-light"))

(defn badge [label classes]
  [:span {:class (str "badge " classes)} label])

(defn conference-badges-row [badges]
  [:div {:class "badge-row"} badges])

(defn top-2 [collection]
  (->> collection
       (sort-by #(second %))
       (reverse)
       (take 2)
       (map first)))

(def roles->sentence-fragment {:dev        "a developer"
                               :devops     "a devops aka admin :)"
                               :ux         "a user experience designer"
                               :qa         "a quality analyst"
                               :ba         "a business analyst"
                               :pm         "a project manager"
                               :sales      "a sales person"
                               :other      "a random person"
                               :recruiting "a recruiter"})

(def roles->badge-label {:dev        "DEV"
                         :devops     "DEVOPS"
                         :ux         "UX"
                         :qa         "QA"
                         :ba         "BA"
                         :pm         "PM"
                         :sales      "Sales"
                         :other      "Other"
                         :recruiting "Recruiting"})

(def experience->sentence-fragment {:rookie       "a rookie"
                                    :beginner     "a beginner"
                                    :intermediate "intermediate"
                                    :advanced     "advanced"
                                    :expert       "an expert"})

(def experience->badge-label {:rookie       "Rookie"
                              :beginner     "Beginner"
                              :intermediate "Intermediate"
                              :advanced     "Advanced"
                              :expert       "Expert"})

(def tags->sentence-fragment {:inspiring    "I am looking for inspiring talks to get awesome ideas for new projects."
                              :entertaining "I want to get entertained and have fun at an awesome after party."
                              :informative  "I am here to learn a lot and watch as many talks as possible."
                              :hires        "I want to meet potential future colleagues and extend my network."
                              :clients      "I am on the hunt for potential clients that are present at this confernece."})

(def tags->badge-label {:inspiring    "Inspiring"
                        :entertaining "Entertaining"
                        :informative  "Informative"
                        :hires        "Potential Hires"
                        :clients      "Potential Clients"})

(defn get-role-sentence [roles]
  (let [top-2-roles (->> roles
                         (top-2)
                         (map roles->sentence-fragment))]
    (str "I am " (first top-2-roles) " or " (second top-2-roles) ".")))

(defn get-experience-sentence [experiences]
  (let [top-2-experience (->> experiences
                              (top-2)
                              (map experience->sentence-fragment))]
    (str "I am " (first top-2-experience) " or " (second top-2-experience) " in my field.")))

(defn get-tags-sentence [tags]
  (let [top-2-tags (->> tags
                        (top-2)
                        (map tags->sentence-fragment))]
    [:div
     [:p (first top-2-tags)]
     [:p (second top-2-tags)]]))

(defn has-non-zero-ratings [ratings-collection]
  (->> ratings-collection
       (map second)
       (filter (complement zero?))
       (not-empty)))

(defn ratings-sentence [collection sentence-fn]
  (if (has-non-zero-ratings collection)
    [:p (sentence-fn collection)]))

(defn average-attendee [aggregated-ratings]
  [:div
   (ratings-sentence (:roles aggregated-ratings) get-role-sentence)
   (ratings-sentence (:experience aggregated-ratings) get-experience-sentence)
   (ratings-sentence (:tags aggregated-ratings) get-tags-sentence)])

(defn- conference-badge [lookup-table]
  (fn [[key count]]
    (let [label (lookup-table key)]
      [badge (str label " (" count ")") "badge-light-blue"])))

(defn badges [coll lookup-table]
  (->> coll
       (filter #(not= 0 (second %)))
       (sort-by #(second %))
       (reverse)
       (map (conference-badge lookup-table))))


(defn conference-badges [aggregated-ratings]
  [:div
   (conference-badges-row (badges (:roles aggregated-ratings) roles->badge-label))
   (conference-badges-row (badges (:experience aggregated-ratings) experience->badge-label))
   (conference-badges-row (badges (:tags aggregated-ratings) tags->badge-label))])

(defn conference-average-attendee [aggregated-ratings]
  [:div
   (panel/info-panel "glyphicon-user" "I am your average attende" (average-attendee aggregated-ratings) (conference-badges aggregated-ratings))])


(defn display-aggregated-ratings [conference]
  (let [aggregated-ratings (if (util/is-future-conference? conference) (:average-series-rating conference) (:aggregated-ratings conference))
        overall-avg (get-in aggregated-ratings [:overall :avg])
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
       (panel/range-panel overall-avg-percentage overall-avg "Overall" overall-ratings-str "bg-mint" "glyphicon-thumbs-up")
       (panel/range-panel talks-avg-percentage talks-avg "Talks" talks-ratings-str "bg-purple" "glyphicon-user")]
      [:div {:class "col-lg-6 col-md-6 col-sm-6"}
       (panel/range-panel venue-avg-percentage venue-avg "Venue" venue-ratings-str "bg-pink" "glyphicon-home")
       (panel/range-panel networking-avg-percentage networking-avg "Networking" networking-ratings-str "bg-green" "glyphicon-glass")]]
     (conference-average-attendee aggregated-ratings)]))



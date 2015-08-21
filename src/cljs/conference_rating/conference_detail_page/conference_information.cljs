(ns conference-rating.conference-detail-page.conference-information)

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

(defn display-conference-information [conference]
  [:div {:class "row conference-information-container bg-light cl-dark"}
   (series-tag (:series conference))
   [:h1(:name conference)]
   (conference-dates (:from conference) (:to conference))
   [:h4 (:description conference)]
   (link (:link conference))])

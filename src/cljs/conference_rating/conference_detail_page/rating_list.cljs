(ns conference-rating.conference-detail-page.rating-list
  (:require [conference-rating.util :as util]))

(defn author-text [user]
  (if (nil? (:firstName user))
    (:email user)
    (str (:firstName user) " " (:lastName user) " (" (:email user) ")")))

(defn display-rating [rating]
   [:div {:class "rating"}
    [:div {:class "row bg-light"}
     [:div {:class "col-lg-12 col-md-12"}
      [:p {:class "text-bold" :data-e2e "rating-author"} (author-text (:user rating))]
      [:p {:data-e2e "rating-comment"} (util/formatted-text (get-in rating [:comment :comment]))]]]])

(defn has-comment [rating]
  (not= "" (get-in rating [:comment :comment])))

(defn display-rating-list [conference-ratings]
  [:div {:class "container-fluid"} (map display-rating (filter has-comment conference-ratings))])

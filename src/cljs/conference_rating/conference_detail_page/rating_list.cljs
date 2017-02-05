(ns conference-rating.conference-detail-page.rating-list
  (:require [conference-rating.util :as util]))

(defn author-text [rating]
  (if (not= "" (:name rating))
    (:name rating)
    (util/user-text (:user rating))))

(defn display-rating [rating]
   [:div {:class "rating"}
    [:div {:class "row bg-light"}
     [:div {:class "col-lg-12 col-md-12"}
      [:p {:class "text-bold"} (author-text rating)]
      [:p (util/formatted-text (get-in rating [:comment :comment]))]]]])

(defn has-comment [rating]
  (not= "" (get-in rating [:comment :comment])))

(defn display-rating-list [conference-ratings]
  [:div {:class "container-fluid"} (map display-rating (filter has-comment conference-ratings))])

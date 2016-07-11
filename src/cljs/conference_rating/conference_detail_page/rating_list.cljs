(ns conference-rating.conference-detail-page.rating-list
  (:require [conference-rating.util :as util]))

(defn display-rating [rating]
   [:div {:class "rating"}
    [:div {:class "row bg-light"}
     [:div {:class "col-lg-12 col-md-12"}
      [:h5 (get-in rating [:comment :name])]
      [:p (util/formatted-text (get-in rating [:comment :comment]))]]]])

(defn hasComment [rating]
  (not (and (= (get-in rating [:comment :name]) "") (= (get-in rating [:comment :comment]) ""))))

(defn display-rating-list [conference-ratings]
  [:div {:class "container-fluid pad-top"} (map display-rating (filter hasComment conference-ratings))])

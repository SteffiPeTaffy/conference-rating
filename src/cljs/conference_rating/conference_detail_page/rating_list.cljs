(ns conference-rating.conference-detail-page.rating-list)

(defn display-rating [rating]
   [:div {:class "row bg-light"}
    [:div {:class "col-lg-12 col-md-12"}
      (if (:recommended rating) [:p "I would go again!"])
      [:p (get-in rating [:comment :name])]
      [:p (get-in rating [:comment :comment])]]])

(defn display-rating-list [conference-ratings]
  [:div {:class "container-fluid pad-top"} (map display-rating conference-ratings)])

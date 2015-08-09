(ns conference-rating.rating
  (:require [conference-rating.panel :as panel]))


(defn display-rating [conference-ratings-entry]
  (panel/light-panel ""
                     [:div {:key (:_id conference-ratings-entry)}
                        [:p {:class "text-right"} (str "By " (:author conference-ratings-entry))]
                        [:p (str (:stars conference-ratings-entry) " out of 5 stars")]
                        [:p (:comment conference-ratings-entry)]]))


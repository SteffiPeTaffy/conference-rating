(ns conference-rating.conference)

(defn display-conference-overview [conference]
  [:div {:class "jumbotron bg-mint cl-light"}
   [:h1(:name conference)]
   [:p (:description conference)]])

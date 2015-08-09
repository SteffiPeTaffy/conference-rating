(ns conference-rating.conference.conference-list
 (:require [conference-rating.panel :as panel]))

(defn display-conference-list-item [conference-list-entry]
  [:div {:key (:_id conference-list-entry) :class "panel"}
   [:div {:class "panel-heading bg-purple cl-light"}
    [:h4 {:class "mar-no"} (:name conference-list-entry)]
    [:p "10.09.2015 - 13.09.2015"]]
   [:div {:class "panel-body"}
    [:div {:class "text-lg-right"}
     [:a {:class "btn btn-lg btn-primary glyphicon glyphicon-pencil" :href (str "#/conferences/" (:_id conference-list-entry))} "rate"]]
    [:p {:class "text-muted"} (:description conference-list-entry)]
    [:a {:href (str "#/conferences/" (:_id conference-list-entry))} "go to conference overview"]]])

(defn display-conference-list [conference-list]
  [:div {:class "container conferences-container"}
   [:h1 "Conferences"]
   [:div {:class "add-conference-container"}
    [:a {:class "btn btn-primary glyphicon glyphicon-plus" :href "#/add-conference"} "add conference"]]
   [:div (map display-conference-list-item conference-list)]
   [:div (panel/range-panel 55 "Okay" "Hello Banana was happy :)")]])
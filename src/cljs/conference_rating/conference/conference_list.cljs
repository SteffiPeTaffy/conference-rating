(ns conference-rating.conference.conference-list
 (:require [conference-rating.panel :as panel]))

(defn display-conference-list-item [conference-list-entry]
  [:div {:key (:_id conference-list-entry) :class "panel"}
   [:div {:class "panel-heading bg-purple cl-light"}
    [:h4 {:class "mar-no"} (:name conference-list-entry)]
    [:p "10.09.2015 - 13.09.2015"]]
   [:div {:class "panel-body"}
    [:div {:class "text-lg-right"}
     [:button {:class "btn btn-sm btn-primary"} "add rating"]]
    [:p {:class "text-muted"} (:description conference-list-entry)]
    [:a {:href (str "#/conferences/" (:_id conference-list-entry))} "go to conference overview"]]])

(defn display-conference-list [conference-list]
  [:div {:class "container"}
   [:h1 "Conferences"]
   [:div (map display-conference-list-item conference-list)]
   [:div (panel/range-panel 55 "Okay" "Hello Banana was happy :)")]
   [:h2 "Actions"]
   [:ul
    [:li [:a {:href "#/add-conference"} "Add conference"]]]])
(ns conference-rating.conference.conference-list
   (:require [conference-rating.panel :as panel]
             [conference-rating.header :as header]))

(defn display-conference-list-item [conference-list-entry]
  [:div {:key (:_id conference-list-entry) :class "panel"}
   [:div {:class "panel-heading bg-green cl-light"}
    [:h4 {:class "mar-no"} (:name conference-list-entry)]
    [:p "10.09.2015 - 13.09.2015"]]
   [:div {:class "panel-body"}
    [:div {:class "text-lg-right"}
     [:a {:class "btn btn-green glyphicon glyphicon-pencil" :href (str "#/conferences/" (:_id conference-list-entry) "/add-rating")} "rate"]]
    [:p {:class "text-muted"} (:description conference-list-entry)]
    [:a {:href (str "#/conferences/" (:_id conference-list-entry))} "go to conference overview"]]])

(defn display-conference-list [conference-list]
  [:div
   (header/header)
   (header/intro-header)
   [:div {:class "container-fluid"}
    [:div (map display-conference-list-item conference-list)]]])

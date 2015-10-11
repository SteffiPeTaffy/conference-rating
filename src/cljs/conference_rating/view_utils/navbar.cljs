(ns conference-rating.view-utils.navbar)

(defn nav-bar []
  [:nav {:class "navbar navbar-inverse navbar-fixed-top"}
   [:div {:class "container-fluid"}
    [:div {:class "navbar-header"}
     [:a {:class "navbar-brand" :href "#"}
      [:span {:class "cl-yellow"} "conference"]
      [:span " voices"]
      [:span {:class "glyphicon glyphicon-bullhorn"}]]]]])

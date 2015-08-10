(ns conference-rating.header)


(defn header []
  [:nav {:class "navbar navbar-inverse navbar-fixed-top"}
   [:div {:class "container-fluid"}
    [:div {:class "navbar-header"}
     [:a {:class "navbar-brand" :href="#"}
      [:span "conference" {:class "cl-yellow"}]
      [:span " voices"]
      [:span {:class "glyphicon glyphicon-bullhorn"}]]]]])

(defn intro-header []
  [:nav {:class "navbar navbar-inverse intro"}
   [:div {:class "overlay-yellow"}
     [:div {:class "container-fluid"}
      [:div {:class "text-lg-center intro-header-title"} "Give your favorite conference a voice!"]
      [:div {:class "intro-action-container"}
       [:a {:class "btn btn-primary btn-lg intro-action-btn" :href "#/add-conference"} "new conference"]]]]])
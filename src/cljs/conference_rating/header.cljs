(ns conference-rating.header)


(defn nav-bar []
  [:nav {:class "navbar navbar-inverse navbar-fixed-top"}
   [:div {:class "container-fluid"}
    [:div {:class "navbar-header"}
     [:a {:class "navbar-brand" :href="#"}
      [:span {:class "cl-yellow"} "conference"]
      [:span " voices"]
      [:span {:class "glyphicon glyphicon-bullhorn"}]]]]])


(defn legend [name bg-color]
  [:span {:class (str "legend cl-light " bg-color)} name])

(defn search-bar []
  [:nav {:class "navbar navbar-inverse search-bar"}
  [:div {:class "row"}
   [:div {:class "col-md-6"} "search conferences"]
   [:div {:class "col-md-6"}
    [:div {:class "legend-bar"}
     (legend "DEV" "bg-dev")
     (legend "DEV OPS" "bg-devops")
     (legend "UX" "bg-ux")
     (legend "QA" "bg-qa")
     (legend "BA" "bg-ba")
     (legend "PM" "bg-pm")
     (legend "SALES" "bg-sales")
     (legend "RECRUITING" "bg-recruting")
     (legend "OTHER" "bg-other")]]]])

(defn add-conference-bar []
  [:div {:class "add-conference-btn-container"}
   [:a {:class "btn btn-md btn-orange mar-bottom" :href "#/add-conference"} "new conference"]])

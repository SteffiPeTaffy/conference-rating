(ns conference-rating.conference.conference-list-entry
  (:require [conference-rating.panel :as panel]))

(defn progress [percentage bg-color]
  [:div {:style {:width (str percentage "%")} :class (str "progressbar progressbar-light " bg-color)}])

(defn display-conference-list-item [conference-list-entry]
  [:div {:key (:_id conference-list-entry) :class "col-lg-4 conference-item-container"}
   [:div {:class "panel panel-heading bg-light cl-dark"}
    [:div {:class "row conference-row"}
     [:div {:class "col-md-6"}
      [:div {:class "series-tag-container"}[:span {:class "series-tag"} "DEVOXX"]]
      [:h4 (:name conference-list-entry)]
      [:p "10.09.2015 - 13.09.2015"]]
     [:div {:class "col-md-6 conference-rating-column"}
      [:div {:class "text-lg-right"} "banana"]]]
    [:div {:class "bottom-line"}]]
   [:div {:class "panel-body  bg-light"}
    [:div {:class "row"}
     [:div {:class "col-md-8"}
      [:p {:class "text-muted"} (:description conference-list-entry)]
      [:a {:href (str "#/conferences/" (:_id conference-list-entry))} "go to conference overview"]]
     [:div {:class "col-md-4 conference-overall-rating-conatiner"}
      [:div {:class "conference-overall-rating"} (panel/range-panel 83 "Overall" "12 ratings" "bg-dark-lightened" "glyphicon-thumbs-up")]
      [:div {:class "text-lg-right"}
       [:a {:class "btn btn-sm btn-orange glyphicon glyphicon-pencil voice-btn" :href (str "#/conferences/" (:_id conference-list-entry) "/add-rating")} "rate"]]]]]
   [:div {:class "panel-footer"}
    [:div {:class "row"}
     [:div {:class "col-md-12"}
      [:div {:class "progress-xs"}
       (progress 72 "bg-dev")
       (progress 23 "bg-devops")
       (progress 5 "bg-ux")]]]]])
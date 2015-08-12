(ns conference-rating.conference.conference-list-entry)

(defn display-conference-list-item [conference-list-entry]
  [:div {:key (:_id conference-list-entry) :class "col-lg-4 conference-item-container"}
   [:div {:class "panel panel-heading bg-light cl-dark"}
    [:h4 {:class "mar-no"} (:name conference-list-entry)]
    [:p "10.09.2015 - 13.09.2015"]
    [:div {:class "bottom-line"}]]
   [:div {:class "panel-body  bg-light"}
    [:div {:class "row"}
     [:div {:class "col-md-8"}
      [:p {:class "text-muted"} (:description conference-list-entry)]
      [:a {:href (str "#/conferences/" (:_id conference-list-entry))} "go to conference overview"]]
     [:div {:class "col-md-4"}
      [:div {:class "text-lg-right"}
       [:a {:class "btn btn-sm btn-orange glyphicon glyphicon-pencil" :href (str "#/conferences/" (:_id conference-list-entry) "/add-rating")} "rate"]]]]]])
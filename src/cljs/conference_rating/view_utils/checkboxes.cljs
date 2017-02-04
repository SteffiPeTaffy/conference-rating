(ns conference-rating.view-utils.checkboxes)

(defn checkbox-description-right [id input-label input-label-description]
  [:div {:class "row role-checkbox-container"}
   [:div {:class "col-lg-2 col-md-2 col-sm-2 col-xs-2"}
    [:input {:field :checkbox :type "checkbox" :id id}]
    [:label {:for id}
     [:span {:class "checkbox"}]]]
   [:label {:for id :class "block-label"}
    [:div {:class "col-lg-10 col-md-10 col-sm-10 col-xs-10"}
     [:p {:class "text-lg-left role-label"} input-label]
     [:p {:class "text-lg-left role-description"} input-label-description]]]])

(defn role-checkbox [id input-label input-label-description]
  [:div {:class "col-lg-4 col-md-6 col-sm-6 col-xs-12"}
   (checkbox-description-right id input-label input-label-description)])

(defn experience-checkbox [id input-label]
  [:div {:class "col-lg-2 col-md-2 col-sm-3 col-xs-4"}
   [:input {:field :checkbox :type "checkbox" :id id}]
   [:label {:for id}
    [:p {:class "text-lg-center"} input-label]
    [:span {:class "checkbox"}]]])

(defn filter-checkbox [id input-label]
  [:div {:class "row"}
   [:div {:class "col-lg-2 col-md-2 col-sm-2 col-xs-2"}
    [:input {:field :checkbox :type "checkbox" :id id}]
    [:label {:for id}
     [:span {:class "checkbox"}]]]
   [:label {:for id :class "block-label"}
    [:div {:class "col-lg-10 col-md-10 col-sm-10 col-xs-10"}
     [:p {:class "text-lg-left role-label"} input-label]]]])
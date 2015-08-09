(ns conference-rating.panel)

(defn panel [heading-label body]
  [:div {:class "panel"}
   [:div {:class "panel-heading"}
    [:h3 {:class "panel-title"} heading-label]]
   [:div {:class "panel-body"} body]])

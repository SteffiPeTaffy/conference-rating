(ns conference-rating.panel)

(defn coloured-panel [heading-label body heading-classes body-classes]
  [:div {:class "panel"}
   [:div {:class (str "panel-heading " heading-classes)}
    [:h3 {:class "panel-title"} heading-label]]
   [:div {:class (str "panel-body " body-classes)} body]])

(defn mint-panel [heading-label body]
  (coloured-panel heading-label body "bg-mint cl-light" "bg-mint cl-light"))

(defn purple-panel [heading-label body]
  (coloured-panel heading-label body "bg-purple cl-light" "bg-purple cl-light"))

(defn light-panel [heading-label body]
  (coloured-panel heading-label body "" ""))

(defn range-panel [percentage name comment]
  [:div {:class "panel panel-mint"}
   [:div {:class "pad-all"}
    [:div {:class "media-left"}
     [:div {:class "icon-wrap"}
      [:span {:class "glyphicon glyphicon-star"}]]]
    [:div {:class "media-body"}
     [:div {:class "h3-media-heading"} (str percentage "%")]
     [:div {:class "text-uppercase"} name]]]
   [:div {:class "progress-xs"}
    [:div {:style {:width (str percentage "%")} :class "progessbar progressbar-light" }]]
   [:div {:class "pad-all text-right"}
    [:span {:class "text-semi"} comment]]])
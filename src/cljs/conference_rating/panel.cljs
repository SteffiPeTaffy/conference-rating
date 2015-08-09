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
(ns conference-rating.view-utils.panel)

(defn coloured-panel [heading-label body heading-classes body-classes]
  [:div {:class "panel"}
   [:div {:class (str "panel-heading " heading-classes)}
    [:h3 {:class "panel-title"} heading-label]]
   [:div {:class (str "panel-body " body-classes)} body]])

(defn light-panel [heading-label body]
  (coloured-panel heading-label body "" ""))

(defn mini-panel [number icon year color]
 (if number
  [:div {:class (str "panel mini-panel cl-light " color )}
   [:div {:class "media-container"}
    [:div {:class "media-left"}
     [:div {:class "media-container icon-wrap glyphicon-mini"}
      [:span {:class (str "glyphicon " icon)}]]]
    [:div {:class "media-body"}
     [:h3 {:class "media-body text-lg-center"} (str number)]]]
     [:div {:class "media-container media-body text-lg-center"} (if year [:div {:class "text-lg-center text-semi"}(str "( " year " )")])]]))

(defn mini-panel-recommendations [number year]
 (mini-panel number "glyphicon-star" year "bg-dark-lightened"))

(defn mini-panel-voices [number year]
 (mini-panel number "glyphicon-bullhorn" year "bg-yellow-lightened"))

(defn range-panel [percentage absolute name comment panel-classes icon]
  [:div {:class (str "panel range-panel " panel-classes)}
   [:div {:class "media-container"}
    [:div {:class "media-left"}
     [:div {:class "media-container icon-wrap icon-md"}
      [:span {:class (str "glyphicon " icon)}]]]
    [:div {:class "media-body"}
     [:h3 {:class "media-body range-label"} (str absolute " / 4")]
     [:div {:class "text-uppercase"} name]]]
   [:div {:class "progress-xs"}
    [:div {:style {:width (str percentage "%")} :class "progressbar progressbar-light"}]]
   [:div {:class "media-container media-body text-lg-right"}
    [:span {:class "text-semi"} comment]]])

(defn icon-panel [icon number label color]
  [:div {:class "panel"}
   [:div {:class (str "panel-body text-lg-center " color)}
    [:div {:class "media-container"}
     [:div {:class "media-center"}
      [:div {:class "media-container icon-wrap icon-lg"}
       [:span {:class (str "glyphicon " icon)}]]]
     [:div {:class "media-body"}
      [:h3 number]
      [:h3 label]]]]])

(defn info-panel [icon title body-information footer-information]
  [:div {:class (str "panel bg-blue cl-light info-panel")}
   [:div {:class "panel-heading text-lg-left"}
    [:div {:class "media-container"}
     [:div {:class "media-left"}
      [:div {:class "media-container icon-wrap icon-md"}
       [:span {:class (str "glyphicon " icon)}]]]
     [:div {:class "media-body"}
      [:h3 title]]]]
   [:div {:class "panel-body text-lg-left"} body-information]
   [:div {:class "panel-footer text-lg-left"} footer-information]])
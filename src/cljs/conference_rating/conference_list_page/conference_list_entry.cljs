(ns conference-rating.conference-list-page.conference-list-entry
  (:require [conference-rating.view-utils.panel :as panel]
            [conference-rating.util :as util]
            [cljs-time.core :as t]))

(defn series-tag [series-tag]
  (if (not (nil? series-tag))
      [:div {:class "series-tag-container"}[:span {:class "series-tag"} series-tag]]
      [:div {:class "series-tag-container"}[:span {:class "series-tag series-tag-hidden"} ""]]))

(defn title [name]
  [:h4 {:class "conference-title"} (or name "untitled conference")])

(defn description [description]
  [:div {:class "text-muted conference-description"} description])

(defn link [link]
  [:p {:class "conference-link"}[:a {:href link :class "conference-link"} link]])

(defn overall-rating [overall-rating]
  (let [overall-rating-percentage (* (/ (:avg overall-rating) 4) 100)]
    [:div {:class "conference-overall-rating"} (panel/range-panel overall-rating-percentage (:avg overall-rating) "Overall" (str (:count overall-rating) " ratings") "bg-dark-lightened" "glyphicon-thumbs-up")]))

(defn add-rating-button [id]
  [:div {:class "text-lg-right"}
   [:a {:class "btn btn-sm btn-orange voice-btn" :href (str "#/conferences/" id "/add-rating")} "give it your voice"]])

(defn roles [percentage bg-color role-name]
  [:div {:style {:width (str percentage "%")} :class (str "progressbar progressbar-light roles " bg-color)}
   (if (> percentage  15) [:p role-name])])

(defn perc [total value]
  (* 100 (/ value total)))

(defn roles-bar [rolesMap]
  (let [count (->> rolesMap
                  (vals)
                  (reduce +))]
    [:div {:class "progress-md"}
     (roles (perc count (:dev rolesMap)) "bg-dev" "DEV")
     (roles (perc count (:devops rolesMap)) "bg-devops" "DEV OPS")
     (roles (perc count (:ux rolesMap)) "bg-ux" "UX")
     (roles (perc count (:qa rolesMap)) "bg-qa" "QA")
     (roles (perc count (:ba rolesMap)) "bg-ba" "BA")
     (roles (perc count (:pm rolesMap)) "bg-pm" "PM")
     (roles (perc count (:sales rolesMap)) "bg-sales" "SALES")
     (roles (perc count (:recruiting rolesMap)) "bg-recruting" "RECRUITING")
     (roles (perc count (:other rolesMap)) "bg-other" "OTHER")]))

(defn display-overall-rating [conference]
  (if (not (util/is-future-conference? conference))
    [:div {:class "col-lg-4 col-md-4 col-sm-5 conference-overall-rating-container"}
     (overall-rating (get-in conference [:aggregated-ratings :overall]))
     (add-rating-button (:_id conference))]
    [:div {:class "col-lg-4 col-md-4 col-sm-5 conference-overall-rating-container"}
     [:div {:class "series-average-ratings panel-heading"} "Series Average Rating"]
     (overall-rating (get-in conference [:average-series-rating :overall]))
     ]))

(defn display-recommendations-votes [conference]
  (when-not (util/is-future-conference? conference)
    [:div {:class "col-lg-4 col-md-4 col-sm-4 col-xs-4 recommendations-votes-panel"}
     (panel/mini-panel-recommendations (get-in conference [:aggregated-ratings :recommendations]) nil)
     (panel/mini-panel-voices (get-in conference [:aggregated-ratings :number-of-ratings]) nil)]))

(defn display-conference-list-item [conference]
  [:div {:key (:_id conference) :class "col-lg-4 col-md-6 col-sm-6 col-xs-12 conference-item-container"}
   [:div {:class "panel panel-heading bg-light cl-dark"}
    [:div {:class "row conference-row"}
     [:div {:class "col-lg-8 col-md-8 col-sm-8 col-xs-8"}
      (series-tag (:series conference))
      [:a {:href (str "#/conferences/" (:_id conference))}
       (title (:name conference))
       (util/from-to-dates (:from conference) (:to conference))]]
     (display-recommendations-votes conference)]
    [:div {:class "bottom-line"}]]
   [:div {:class "panel-body  bg-light"}
    [:div {:class "row"}
     [:div {:class "col-lg-8 col-md-8 col-sm-7"}
      (description (util/formatted-text (:description conference)))
      (link (:link conference))]
     (display-overall-rating conference)]]
   [:div {:class "panel-footer"}
    [:div {:class "row"}
     [:div {:class "col-md-12"}
      (roles-bar (get-in conference [:aggregated-ratings :roles]))]]]])


(ns conference-rating.conference-list-page.conference-list-entry
  (:require [conference-rating.view-utils.panel :as panel]
            [conference-rating.util :as util]
            [conference-rating.view-utils.conference :as conference-util]
            [goog.string :as gstring]))

(defn series-tag [series-tag]
  (if-not (nil? series-tag)
    [:div {:class "series-tag-container"} [:span {:class "series-tag"} series-tag]]
    [:div {:class "series-tag-container"} [:span {:class "series-tag series-tag-hidden"} ""]]))

(defn title [name]
  [:h4 {:class "conference-title" :data-e2e "text-conference-name"} (or name "untitled conference")])

(defn description [description]
  [:div {:class "text-muted conference-description"} description])

(defn overall-rating [overall-rating]
  [:div {:class "conference-overall-rating"} (panel/range-panel-small (:avg overall-rating) "Overall" "bg-dark-lightened" "glyphicon-thumbs-up")])

(defn add-rating-button [id]
  [:div {:class "text-lg-right"}
   [:a {:class "btn btn-sm btn-orange voice-btn" :href (str "#/conferences/" id "/add-rating")}
    [:span {:class "glyphicon glyphicon-bullhorn hidden-sm"}]
    "Voice"]])

(defn roles [percentage bg-color role-name]
  [:div {:title role-name :style {:width (str percentage "%")} :class (str "progressbar progressbar-light roles " bg-color)}
   (if (> percentage 15) [:p role-name])])

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
  (let [ratings-key (conference-util/ratings-key-for conference)
        has-ratings? (> (get-in conference [ratings-key :number-of-ratings]) 0)
        base-classes "col-lg-4 col-md-4 col-sm-4 col-xs-4 conference-overall-rating-container"]
    [:div {:class (if has-ratings? base-classes (str base-classes " no-ratings"))}
     (overall-rating (get-in conference [ratings-key :overall]))]))

(defn display-add-rating-button [conference]
  (if (not (conference-util/is-future-conference? conference))
    [:div {:class "col-lg-3 col-md-3 col-sm-3 conference-overall-rating-container"}
     (add-rating-button (:_id conference))]))

(defn display-recommendations-votes [conference]
  (let [ratings-key (conference-util/ratings-key-for conference)
        has-ratings? (> (get-in conference [ratings-key :number-of-ratings]) 0)
        base-classes "col-lg-4 col-md-4 col-sm-4 hidden-xs recommendations-votes-panel"]
    [:div {:class (if has-ratings? base-classes (str base-classes " no-ratings"))}
     (panel/mini-panel-recommendations (get-in conference [ratings-key :recommendations]) nil)
     (panel/mini-panel-voices (get-in conference [ratings-key :number-of-ratings]) nil)]))

(defn display-conference-list-item [conference]
  [:div {:key (:_id conference) :class "col-lg-4 col-md-6 col-sm-6 col-xs-12 conference-item-container"}
   [:div {:class "panel panel-heading bg-light cl-dark"}
    [:div {:class "row conference-row"}
     [:a {:href (str "#/conferences/" (:_id conference))}
      [:div {:class "col-lg-8 col-md-8 col-sm-8 col-xs-8"}
       (series-tag (:series conference))
       (title (:name conference))
       (util/from-to-dates (:from conference) (:to conference))
       (util/location (:location conference))]
     (display-recommendations-votes conference)
     (display-overall-rating conference)]]
    [:div {:class "bottom-line"}]]
   [:div {:class "panel-body  bg-light"}
    [:div {:class "row"}
     [:div {:class "col-lg-9 col-md-9 col-sm-9"}
      (description (util/formatted-text (:description conference)))
      (util/link (:link conference))]
     (display-add-rating-button conference)]]
   [:div {:class "panel-footer"}
    [:div {:class "row"}
     [:div {:class "col-md-12"}
      (roles-bar (get-in conference [(conference-util/ratings-key-for conference) :roles]))]]]])


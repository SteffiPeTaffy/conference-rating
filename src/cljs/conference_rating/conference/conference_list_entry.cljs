(ns conference-rating.conference.conference-list-entry
  (:require [conference-rating.panel :as panel]))

(def aggregated-ratings
  {:aggregated-ratings {:number-of-ratings 2
                        :recommendations 1
                        :overall {:avg 4 :count 2}
                        :talks {:avg 4 :count 2}
                        :venue {:avg 2.5 :count 2}
                        :community {:avg 4.5 :count 2}
                        :roles {:dev 2
                                :devops 2
                                :ux 2
                                :qa 1
                                :ba 1
                                :pm 1
                                :sales 1
                                :recruting 1
                                :other 1}
                        :tags {
                               :inspiring 2
                               :entertaining 2
                               :learning 2
                               :potential-hires 1
                               :potential-clients 1}}})

(defn series-tag [series-tag]
  (if (not (nil? series-tag))
      [:div {:class "series-tag-container"}[:span {:class "series-tag"} series-tag]]
      [:div {:class "series-tag-container"}[:span {:class "series-tag series-tag-hidden"} ""]]))

(defn title [name]
  (if (not (nil? name))
    [:h4 name]
    [:h4 "untitled conference"]))

(defn conference-dates [from-date to-date]
  (cond
    (and from-date from-date) [:p (str from-date " - " to-date)]
    from-date [:p from-date]
    :else [:p "TBD"]))

(defn description [description]
  [:p {:class "text-muted conference-description"} description])

(defn link [link]
  [:a {:href link :class "conference-link"} link])

(defn overall-rating [overall-rating]
  (let [overall-rating-percentage (* (/ (:avg overall-rating) 5) 100)]
    [:div {:class "conference-overall-rating"} (panel/range-panel overall-rating-percentage (:avg overall-rating) "Overall" (str (:count overall-rating) " ratings") "bg-dark-lightened" "glyphicon-thumbs-up")]))

(defn add-rating-button [id]
  [:div {:class "text-lg-right"}
   [:a {:class "btn btn-sm btn-orange voice-btn" :href (str "#/conferences/" id "/add-rating")} "give it your voice"]])


(defn roles [percentage bg-color]
  [:div {:style {:width (str percentage "%")} :class (str "progressbar progressbar-light " bg-color)}])

(defn perc [total value]
  (* 100 (/ value total)))

(defn roles-bar [rolesMap]
  (let [count (->> rolesMap
                  (vals)
                  (reduce +))]
    [:div {:class "progress-xs"}
     (roles (perc count (:dev rolesMap)) "bg-dev")
     (roles (perc count (:devops rolesMap)) "bg-devops")
     (roles (perc count (:ux rolesMap)) "bg-ux")
     (roles (perc count (:qa rolesMap)) "bg-qa")
     (roles (perc count (:ba rolesMap)) "bg-ba")
     (roles (perc count (:pm rolesMap)) "bg-pm")
     (roles (perc count (:sales rolesMap)) "bg-sales")
     (roles (perc count (:recruting rolesMap)) "bg-recruting")
     (roles (perc count (:other rolesMap)) "bg-other")]))

(defn display-conference-list-item [simple-conference]
  (let [conference (merge simple-conference aggregated-ratings)]
    [:div {:key (:_id conference) :class "col-lg-4 conference-item-container"}
     [:div {:class "panel panel-heading bg-light cl-dark"}
      [:div {:class "row conference-row"}
       [:div {:class "col-md-8"}
        (series-tag (:series-tag conference))
        [:a {:href (str "#/conferences/" (:_id conference))}
         (title (:name conference))
         (conference-dates (:from conference) (:to conference))]]
       [:div {:class "col-md-2"}
        (panel/mini-panel-recommendations (get-in conference [:aggregated-ratings :recommendations]) 2010)]
       [:div {:class "col-md-2"}
        (panel/mini-panel-voices (get-in conference [:aggregated-ratings :number-of-ratings]) 2222)]]
      [:div {:class "bottom-line"}]]
     [:div {:class "panel-body  bg-light"}
      [:div {:class "row"}
       [:div {:class "col-md-8"}
        (description (:description conference))
        (link (:link conference))]
       [:div {:class "col-md-4 conference-overall-rating-conatiner"}
        (overall-rating (get-in conference [:aggregated-ratings :overall]))
        (add-rating-button (:_id conference))]]]
     [:div {:class "panel-footer"}
      [:div {:class "row"}
       [:div {:class "col-md-12"}
        (roles-bar (get-in conference [:aggregated-ratings :roles]))]]]]))


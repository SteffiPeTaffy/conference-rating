(ns conference-rating.conference.conference-list-entry
  (:require [conference-rating.panel :as panel]))

(def ratings
  [{:rating {:recommended true
             :tags ["inspiring", "entertaining", "learning", "potential-hires", "potential-clients"]
             :roles ["DEV", "DEVOPS", "UX", "QA", "BA", "PM", "SALES", "RECRUITING", "OTHER"]
             :overall 5
             :talks 5
             :venue 3
             :community 4
             :comment "this was an awesome conference!"
             :author "Steffi"}}
   {:rating {:recommended true
             :tags ["inspiring", "entertaining", "learning"]
             :roles ["DEV", "DEVOPS", "UX"]
             :overall 3
             :talks 3
             :venue 2
             :community 5
             :comment "this was a good conference!"
             :author "Flo"}}])

(def aggregated-ratings
  {:aggregated-ratings {:number-of-ratings 2
                        :recommendations 2
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
                                :recruiting 1
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
  [:div {:class "conference-overall-rating"} (panel/range-panel (:avg overall-rating) "Overall" (str (:count overall-rating) " ratings") "bg-dark-lightened" "glyphicon-thumbs-up")])

(defn add-rating-button [id]
  [:div {:class "text-lg-right"}
   [:a {:class "btn btn-sm btn-orange glyphicon glyphicon-pencil voice-btn" :href (str "#/conferences/" id "/add-rating")} "rate"]])


(defn roles [percentage bg-color]
  [:div {:style {:width (str percentage "%")} :class (str "progressbar progressbar-light " bg-color)}])

(defn roles-bar [ratings]
  [:div {:class "progress-xs"}
   (roles 72 "bg-dev")
   (roles 23 "bg-devops")
   (roles 5 "bg-ux")])

(defn ratings-recommendations [recommendations]
  [:div {:class "text-lg-right"} recommendations])

(defn ratings-ratings [number-of-ratings]
  [:div {:class "text-lg-right"} number-of-ratings])

(defn display-conference-list-item [simple-conference]
  (let [conference (merge simple-conference aggregated-ratings)]
    [:div {:key (:_id conference) :class "col-lg-4 conference-item-container"}
     [:div {:class "panel panel-heading bg-light cl-dark"}
      [:div {:class "row conference-row"}
       [:div {:class "col-md-8"}
        (series-tag (:series-tag conference))
        (title (:title conference))
        (conference-dates (:from-date conference) (:to-date conference))]
       [:div {:class "col-md-2 conference-rating-column"}
        (ratings-recommendations (get-in conference [:aggregated-ratings :recommendations]))]
       [:div {:class "col-md-2 conference-rating-column"}
        (ratings-ratings (get-in conference [:aggregated-ratings :number-of-ratings]))]]
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

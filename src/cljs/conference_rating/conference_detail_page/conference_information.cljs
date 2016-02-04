(ns conference-rating.conference-detail-page.conference-information
  (:require [conference-rating.util :as util]
            [clojure.string :as s]))


(defn series-tag [series-tag]
  (if (not (nil? series-tag))
    [:div {:class "series-tag-container"}[:span {:class "series-tag" :data-e2e "text-conference-series"} series-tag]]))

(defn link [link]
  [:p {:class "conference-link"}[:a {:href link :class "conference-link" :data-e2e "text-conference-link"} link]])

(defn display-conference-information [conference]
  [:div {:class "row conference-information-container bg-light cl-dark"}
   (series-tag (:series conference))
   [:h1 {:data-e2e "text-conference-name"} (:name conference)]
   (util/from-to-dates (:from conference) (:to conference))
   [:h4 {:data-e2e "text-conference-description"} (util/formatted-text (:description conference))]
   (link (:link conference))])

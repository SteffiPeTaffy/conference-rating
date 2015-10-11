(ns conference-rating.add-conference-page.add-conference-page
  (:require [conference-rating.history :as history]
            [ajax.core :as ajax]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [reagent.core :refer [atom]]
            [reagent-forms.core :as forms]
            [conference-rating.util :as util]
            [conference-rating.view-utils.navbar :as navbar]
            [conference-rating.view-utils.header :as header]
            [conference-rating.backend :as backend]
            [conference-rating.view-utils.typeahead :as typeahead]))

(defn form-input [label input]
  [:div {:class "form-group"}
   [:label {:for (:id (second input))} label]
   input])

(defn conference-series-input []
  [:input {:field :text :id :series :class "form-control" :placeholder "Name of the conference series, e.g. EuroClojure for the EuroClojure 2015 conference"}])

(defn conference-series-suggestions [q cb]
  (backend/load-series-suggestions q (fn [x]
                                       (cb x))))

(defn conference-series-template [series]
  (str "<div>" series "</div>"))

(def conference-series-component
  (typeahead/init-typeahead
    conference-series-input
    (typeahead/config {:hint true,
                       :highlight true,
                       :minLength 1})
    (typeahead/data-sets {:name "series",
                          :source conference-series-suggestions
                          :display identity
                          :async true
                          :templates {:suggestion conference-series-template}})))

(def conference-form-template
  [:div
   (form-input "Series" [conference-series-component])
   (form-input "Name" [:input {:field :text :id :name :class "form-control" :placeholder "Name of the conference"}])
   [:div {:class "row"}
    [:div {:class "col-md-6"} (form-input "From" [:div {:field :datepicker :id :from-date :date-format "yyyy/mm/dd" :inline false :auto-close? true}])]
    [:div {:class "col-md-6"} (form-input "To" [:div {:field :datepicker :id :to-date :date-format "yyyy/mm/dd" :inline false :auto-close? true}])]]
   (form-input "Link" [:input {:field :text :id :link :class "form-control" :placeholder "Link to the conference page"}])
   (form-input "Description" [:textarea {:field :textarea :rows 5 :id :description :class "form-control" :placeholder "More information about the conference"}])])

(defn create-conference [data-atom]
  (let [data    @data-atom
        payload {:from        (util/form-date-to-datestr (:from-date data))
                 :to          (util/form-date-to-datestr (:to-date data))
                 :name        (:name data)
                 :series      (:series data)
                 :link        (:link data)
                 :description (:description data)}]
  (ajax/POST "/api/conferences/" {:params          payload
                                  :format          :json
                                  :response-format :json
                                  :keywords?       true
                                  :handler         #(let [conference-id (:_id %)]
                                                     (history/redirect-to (str "/conferences/" conference-id)))
                                  :error-handler   #(js/alert (str "could not create conference" %1))})))
(defn add-conference-page []
  (let [doc (atom {})]
    [:div
     (navbar/nav-bar)
     [:div {:class "container-fluid content-container pad-top"}
      [:div {:class "row"}
       [:div {:class "col-lg-2"}]
       [:div {:class "col-lg-8"}
        [:div {:class "add-conference-form-container bg-light"}
         [forms/bind-fields conference-form-template doc]
         [:button {:class "btn btn-primary btn-md btn-orange" :on-click #(create-conference doc)} "Create"]]]
       [:div {:class "col-lg-2"}]]]]))

(ns conference-rating.conference.add-conference
  (:require [conference-rating.history :as history]
            [ajax.core :as ajax]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [reagent.core :refer [atom]]
            [reagent-forms.core :as forms]))

(defn form-input [label input]
  [:div {:class "form-group"}
   [:label {:for (:id (second input))} label]
   input])

(def conference-form-template
  [:div
   (form-input "Name" [:input {:field :text :id :name :class "form-control" :placeholder "Name of the conference"}])
   (form-input "Series" [:input {:field :text :id :series :class "form-control" :placeholder "Name of the conference series, e.g. EuroClojure for the EuroClojure 2015 conference"}])
   (form-input "From" [:div {:field :datepicker :id :from-date :date-format "yyyy/mm/dd" :inline false :auto-close? true}])
   (form-input "To" [:div {:field :datepicker :id :to-date :date-format "yyyy/mm/dd" :inline false :auto-close? true}])
   (form-input "Link" [:input {:field :text :id :link :class "form-control" :placeholder "Link to the conference page"}])
   (form-input "Description" [:textarea {:field :textarea :rows 5 :id :description :class "form-control" :placeholder "More information about the conference"}])])

(def built-in-formatter (tf/formatters :date-hour-minute-second-ms))

(defn form-date-to-datestr [form-date]
  (tf/unparse built-in-formatter
    (t/date-time (:year form-date) (:month form-date) (:day form-date))))

(defn create-conference [data-atom]
  (let [data    @data-atom
        payload {:from        (form-date-to-datestr (:from-date data))
                 :to          (form-date-to-datestr (:to-date   data))
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
    [:div {:class "container"}
     [:div {:class "panel panel-default"}
      [:div {:class "panel-heading"} "Add conference"]
      [:div {:class "panel-body"}
       [forms/bind-fields conference-form-template doc]
       [:div [:button {:class "btn btn-primary" :on-click #(create-conference doc)} "Create"]]]]]))

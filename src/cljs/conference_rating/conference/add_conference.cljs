(ns conference-rating.conference.add-conference
  (:require [conference-rating.history :as history]
            [ajax.core :as ajax]
            [reagent.core :refer [atom]]
            [reagent-forms.core :as forms]))

(defn form-input [label input]
  [:div {:class "form-group"}
   [:label {:for (:id (second input))} label]
   input])

(def conference-form-template
  [:div
   (form-input "Name" [:input {:field :text :id :name :class "form-control" :placeholder "Name of the conference"}])
   (form-input "From" [:div {:field :datepicker :id :from-date :date-format "yyyy/mm/dd" :inline false :auto-close? true}])
   (form-input "To" [:div {:field :datepicker :id :to-date :date-format "yyyy/mm/dd" :inline false :auto-close? true}])
   (form-input "Link" [:input {:field :text :id :link :class "form-control" :placeholder "Link to the conference page"}])
   (form-input "Description" [:textarea {:field "textarea" :rows 5 :id :description :class "form-control" :placeholder "More information about the conference"}])])

(defn create-conference [form-data]
  (ajax/POST "/api/conferences/" {:params          @form-data
                                  :format          :json
                                  :response-format :json
                                  :keywords?       true
                                  :handler         #(let [conference-id (:_id %)]
                                                     (history/redirect-to (str "/conferences/" conference-id)))
                                  :error-handler   #(js/alert (str "could not create conference" %1))}))
(defn add-conference-page []
  (let [doc (atom {})]
    [:div {:class "container"}
     [:div {:class "panel panel-default"}
      [:div {:class "panel-heading"} "Add conference"]
      [:div {:class "panel-body"}
       [forms/bind-fields conference-form-template doc]
       [:div [:button {:class "btn btn-primary" :on-click #(create-conference doc)} "Create"]]]]]))

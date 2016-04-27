(ns conference-rating.add-conference-page.add-conference-page
  (:require [conference-rating.history :as history]
            [ajax.core :as ajax]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [reagent.core :refer [atom]]
            [reagent-forms.core :as forms]
            [conference-rating.util :as util]
            [conference-rating.backend :as backend]
            [conference-rating.view-utils.navbar :as navbar]
            [conference-rating.view-utils.typeahead :as typeahead]))

(defn form-input [label input]
  [:div {:class "form-group"}
   [:label {:for (:id (second input))} label]
   input])

(defn conference-series-input [data-atom]
  (fn []
    [:input {:field :text :on-blur (fn [e] (swap! data-atom #(assoc % :series (-> e .-target .-value)))) :id :series :class "form-control" :placeholder "Name of the conference series, e.g. EuroClojure for the EuroClojure 2015 conference" :data-e2e "input-conference-series"}]))

(defn conference-series-suggestions [q cb]
  (backend/load-series-suggestions q cb))

(defn conference-series-template [series]
  (str "<div class=\"series-suggestion-template\">"
       "<p>"series"</p>"
       "</div>"))

(defn conference-series-component [data-atom]
  (typeahead/init-typeahead
    (conference-series-input data-atom)
    (typeahead/config {:hint false,
                       :highlight true,
                       :minLength 1})
    (typeahead/data-sets {:name "series",
                          :source conference-series-suggestions
                          :display identity
                          :async true
                          :templates {:suggestion conference-series-template}})))

(defn conference-form-template [data-atom]
  [:div
   (form-input "Series" [(conference-series-component data-atom)])
   (form-input "Name" [:input {:field :text :id :name :class "form-control" :placeholder "Name of the conference" :data-e2e "input-conference-name"}])
   [:div {:class "row"}
    [:div {:class "col-md-6" :data-e2e "date-conference-from"} (form-input "From" [:div {:field :datepicker :id :from-date :date-format "yyyy/mm/dd" :inline false :auto-close? true }])]
    [:div {:class "col-md-6" :data-e2e "date-conference-to"} (form-input "To" [:div {:field :datepicker :id :to-date :date-format "yyyy/mm/dd" :inline false :auto-close? true}])]]
   (form-input "Link" [:input {:field :text :id :link :class "form-control" :placeholder "Link to the conference page" :data-e2e "input-conference-link"}])
   (form-input "Description" [:textarea {:field :textarea :rows 5 :id :description :class "form-control" :placeholder "More information about the conference" :data-e2e "input-conference-description"}])])

(defn create-conference [data-atom]
  (let [data    @data-atom
        payload {:from        (util/form-date-to-datestr (:from-date data))
                 :to          (util/form-date-to-datestr (:to-date data))
                 :name        (:name data)
                 :series      (:series data)
                 :link        (:link data)
                 :description (:description data)}]
  (backend/add-conference payload)))

(defn add&edit-conference-page [initial-data on-click-function]
  (let [doc initial-data
        temporary-broken-nav-bar-empty-list []]
    [:div {:data-e2e "page-add-conference"}
     (navbar/nav-bar temporary-broken-nav-bar-empty-list)
     [:div {:class "container-fluid content-container pad-top"}
      [:div {:class "row"}
       [:div {:class "col-lg-2"}]
       [:div {:class "col-lg-8"}
        [:div {:class "add-conference-form-container bg-light"}
         [forms/bind-fields (conference-form-template doc) doc]
         [:button {:class "btn btn-primary btn-md btn-orange" :on-click #(on-click-function doc) :data-e2e "button-create-conference"} "Create"]]]
       [:div {:class "col-lg-2"}]]]]))

(defn add-conference-page []
  (add&edit-conference-page (atom {}) create-conference))

(defonce edit-conference-data (atom nil))

(defn edit-conference-page []
  (let [confererence-to-edit @edit-conference-data]
    (if confererence-to-edit
      (add&edit-conference-page edit-conference-data #(backend/edit-conference @%))
      (util/display-loading))))
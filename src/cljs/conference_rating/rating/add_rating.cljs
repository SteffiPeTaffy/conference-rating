(ns conference-rating.rating.add-rating
  (:require [reagent-forms.core :as forms]
            [reagent.core :refer [atom]]
            [ajax.core :as ajax]
            [conference-rating.panel :as panel]
            [conference-rating.form :as form]
            [reagent.session :as session]
            [conference-rating.history :as history]
            [conference-rating.header :as header]))

(defn create-rating [form-data conference-id]
  (ajax/POST (str "/api/conferences/" conference-id "/ratings") {:params        @form-data
                                                                 :format        :json
                                                                 :handler       (history/redirect-to (str "/conferences/" conference-id))
                                                                 :error-handler #(js/alert (str "could not create rating" %1))})
  (println @form-data))

(defn recommendation-panel []
  [:div {:class "panel rating-panel-container bg-orange cl-light"}
   [:span
    [:i {:class "glyphicon glyphicon-star"}]
    [:span "I would go again!"]]
   [:div
    [:input {:field :checkbox :type "checkbox" :id :recommended}]
    [:label {:for :recommended}
     [:span {:class "checkbox checkbox-lg"}]]]])

(defn rating-panel-radio-input [id key input-label value]
  [:div
   [:p {:class "text-lg-center"} input-label]
   [:input {:field :radio :value value :type "radio" :id id :name key}]
   [:label {:for id}
    [:span {:class "radio"}]]])

(defn rating-panel [icon data-key panel-label panel-classes]
  [:div {:class (str "panel rating-panel-container " panel-classes)}
   [:span
    [:i {:class (str "glyphicon " icon)}]
    [:span panel-label]]
   [:div {:class "row"}
    [:div {:class "col-lg-3 col-md-3 col-sm-3 col-xs-3"}
     (rating-panel-radio-input (str panel-label "-1") data-key "meh" 1)]
    [:div {:class "col-lg-3 col-md-3 col-sm-3 col-xs-3"}
     (rating-panel-radio-input (str panel-label "-2") data-key "okay" 2)]
    [:div {:class "col-lg-3 col-md-3 col-sm-3 col-xs-3"}
     (rating-panel-radio-input (str panel-label "-3") data-key"good" 3)]
    [:div {:class "col-lg-3 col-md-3 col-sm-3 col-xs-3"}
     (rating-panel-radio-input (str panel-label "-4") data-key "awesome" 4)]]])

(defn role-checkbox [id input-label input-label-description]
  [:div {:class "col-lg-4 col-md-6 col-sm-6 col-xs-12"}
   [:div {:class "row role-checkbox-container"}
    [:div {:class "col-lg-2 col-md-2 col-sm-2 col-xs-2"}
     [:input {:field :checkbox :type "checkbox" :id id}]
     [:label {:for id}
      [:span {:class "checkbox"}]]]
    [:div {:class "col-lg-10 col-md-10 col-sm-10 col-xs-10"}
     [:p {:class "text-lg-left role-label"} input-label]
     [:p {:class "text-lg-left role-description"} input-label-description]]]])

(defn roles-panel []
  [:div {:class (str "panel rating-panel-container bg-light cl-dark")}
   [:span "This conference might be interesting for"]
   [:div {:class "row"}
     (role-checkbox :roles.dev "Devs" "technical interests")
     (role-checkbox :roles.devops "Dev Ops" "devops interests")
     (role-checkbox :roles.ux "Ux" "ux interests")
     (role-checkbox :roles.qa "QAs" "qa interests")
     (role-checkbox :roles.ba "BAs" "ba interests")
     (role-checkbox :roles.pm "PMs" "doing PM stuff all day long")
     (role-checkbox :roles.sales "Sales" "doing sales stuff all day long")
     (role-checkbox :roles.recruiting "Recruiters" "doing recruiting stuff all day long")
     (role-checkbox :roles.other "Others" "doing other stuff all day long")]])

(defn tag-checkbox [id input-label input-label-description]
  [:div {:class "row role-checkbox-container"}
   [:div {:class "col-lg-2 col-md-2 col-sm-2 col-xs-2"}
    [:input {:field :checkbox :type "checkbox" :id id}]
    [:label {:for id}
     [:span {:class "checkbox"}]]]
   [:div {:class "col-lg-10 col-md-10 col-sm-10 col-xs-10"}
    [:p {:class "text-lg-left role-label"} input-label]
    [:p {:class "text-lg-left role-description"} input-label-description]]])

(defn tags-panel []
  [:div {:class (str "panel rating-panel-container bg-light cl-dark")}
   [:span "I found this conference ..."]
   [:div {:class "row"}
    [:div {:class "col-lg-12 col-md-12 col-sm-12 col-xs-12"}
     (tag-checkbox :tags.inspiring "inspriring" "This conference had an impact one me.")
     (tag-checkbox :tags.informative "informative" "I learned a lot during the workshops, sessions and talks.")
     (tag-checkbox :tags.entertaining "entertaining" "The speakers where showmasters and the after party was amazing.")
     (tag-checkbox :tags.hires "good to meet potential hires" "The speakers where showmasters and the after party was amazing.")
     (tag-checkbox :tags.clients "good to meet potential clients" "The speakers where showmasters and the after party was amazing.")]]])

(defn experience-checkbox [id input-label]
 [:div {:class "col-lg-2 col-md-2 col-sm-3 col-xs-2"}
  [:p {:class "text-lg-center"} input-label]
  [:input {:type "checkbox" :id id}]
  [:label {:for id}
   [:span {:class "checkbox"}]]])

(defn experience-panel []
  [:div {:class (str "panel rating-panel-container bg-light cl-dark")}
   [:span "I found this conference suitable for ..."]
   [:div {:class "row"}
    [:div {:class "col-lg-1 col-md-1 col-sm-1 col-xs-1"}]
    (experience-checkbox :experience.rookie "Rookie")
    (experience-checkbox :experience.beginner "Beginner")
    (experience-checkbox :experience.intermediate "Intermediate")
    (experience-checkbox :experience.advanced "Advanced")
    (experience-checkbox :experience.expert "Expert")
    [:div {:class "col-lg-1 col-md-1 col-sm-1 col-xs-1"}]]])

(defn comment-panel []
  [:div {:class (str "panel rating-panel-container bg-light cl-dark")}
   [:span "I want to say ..."]
   [:div {:class "form-group"}
    [:input {:field :text :id :comment.name :type "text" :placeholder "name" :class "form-control"}]
    [:textarea {:field :textarea :id :comment.comment :placeholder "comment" :class "form-control" :rows 13}]]])

(def add-rating-template
   [:div
    [:div {:class "row"}
     [:div {:class "col-lg-8 col-md-6 col-sm-6"}
      (recommendation-panel)
      (roles-panel)
      (experience-panel)
      (comment-panel)]
     [:div {:class "col-lg-4 col-md-6 col-sm-6"}
      (rating-panel "glyphicon-thumbs-up" :rating.overall "Overall" "bg-mint cl-light")
      (rating-panel "glyphicon-user" :rating.talks "Talks" "bg-purple cl-light")
      (rating-panel "glyphicon-home" :rating.venue "Venue" "bg-pink cl-light")
      (rating-panel "glyphicon-glass" :rating.networking "Networking" "bg-green cl-light")
      (tags-panel)]]])


(defn add-rating []
  (let [doc (atom {:recommended false
                   :roles       {:dev        false
                                 :devops     false
                                 :qa         false
                                 :ux         false
                                 :pm         false
                                 :ba         false
                                 :sales      false
                                 :recruiting false
                                 :other      false}
                   :experience  {:rookie       false
                                 :beginner     false
                                 :intermediate false
                                 :advanced     false
                                 :expert       false}
                   :comment     {:name    ""
                                 :comment ""}
                   :rating      {:overall    -1
                                 :talks      -1
                                 :venue      -1
                                 :networking -1}
                   :tags        {:inspiring    false
                                 :informative  false
                                 :entertaining false
                                 :hires        false
                                 :clients      false}})]
    [:div
     (header/nav-bar)
     [:div {:class "container-fluid content-container pad-top"}
      [:div {:class "row add-rating-container"}
       [:div {:class "col-lg-1 col-md-1"}]
       [:div {:class "col-lg-10 col-md-10"}
        [forms/bind-fields add-rating-template doc]
        [:div {:class "text-lg-right"} [:button {:class "btn btn-lg btn-orange" :on-click #(create-rating doc (session/get! :conference-id-to-rate))} "add rating"]]]
       [:div {:class "col-lg-1 col-md-1"}]]]]))


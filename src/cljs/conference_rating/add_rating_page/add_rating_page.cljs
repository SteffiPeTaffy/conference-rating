(ns conference-rating.add-rating-page.add-rating-page
  (:require [reagent-forms.core :as forms]
            [reagent.core :refer [atom]]
            [ajax.core :as ajax]
            [reagent.session :as session]
            [conference-rating.history :as history]
            [conference-rating.util :as util]
            [conference-rating.view-utils.navbar :as navbar]
            [conference-rating.view-utils.checkboxes :as checkboxes]
            [conference-rating.backend :as backend]
            [conference-rating.user-info :as user-info]))

(defn- convert-to-tag-list [m k]
  (update-in m [k] util/checkboxes-to-tag-list))

(defn create-rating [form-data conference-id]
  (let [processed-data (-> @form-data
                           (util/dissoc-in [:comment :authorLabel])
                           (convert-to-tag-list :roles)
                           (convert-to-tag-list :experience)
                           (convert-to-tag-list :tags))]
    (ajax/POST (str "/api/conferences/" conference-id "/ratings") {:params        processed-data
                                                                   :format        :json
                                                                   :handler       #(history/redirect-to (str "/conferences/" conference-id))
                                                                   :error-handler #(js/alert "Could not create rating. Please make sure to be logged into okta and fill at least one field.")
                                                                   :headers       {:X-CSRF-Token (backend/anti-forgery-token)}})))

(defn recommendation-panel []
  [:label {:for :recommended :class "block-label"}
   [:div {:class "panel rating-panel-container bg-orange cl-light"}
    [:span
     [:i {:class "glyphicon glyphicon-star"}]
     [:span "I would go again!"]]
    [:div
     [:input {:field :checkbox :type "checkbox" :id :recommended}]
     [:label {:for :recommended :data-e2e "checkbox-rating-voice"}
      [:span {:class "checkbox checkbox-lg"}]]]]]
  )

(defn rating-panel-radio-input [id key input-label value]
  [:div
   [:input {:field :radio :value value :type "radio" :id id :name key}]
   [:label {:for id}
    [:p {:class "text-lg-center"} input-label]
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
     (rating-panel-radio-input (str panel-label "-3") data-key "good" 3)]
    [:div {:class "col-lg-3 col-md-3 col-sm-3 col-xs-3"}
     (rating-panel-radio-input (str panel-label "-4") data-key "awesome" 4)]]])

(defn roles-panel []
  [:div {:class (str "panel rating-panel-container bg-light cl-dark")}
   [:span "This conference might be interesting for"]
   [:div {:class "row"}
    (checkboxes/role-checkbox :roles.dev "Devs" "which are always looking for some practical tips and bleeding edge technologies.")
    (checkboxes/role-checkbox :roles.devops "Dev Ops" "which like to learn about the administration of systems.")
    (checkboxes/role-checkbox :roles.ux "UX" "that are all about user experience and visual designs.")
    (checkboxes/role-checkbox :roles.qa "QAs" "that are interested in getting input on quality assurance.")
    (checkboxes/role-checkbox :roles.ba "BAs" "which love to get more insight about story writing, relationship building and more.")
    (checkboxes/role-checkbox :roles.pm "PMs" "that want to know how to manage creative people.")
    (checkboxes/role-checkbox :roles.sales "Sales" "who want to socialize with other sales people to support our clients even better.")
    (checkboxes/role-checkbox :roles.recruiting "Recruiters" "that are on the hunt to find us the brightest colleagues.")
    (checkboxes/role-checkbox :roles.other "Others" "that have other interests :)")]])

(defn tags-panel []
  [:div {:class (str "panel rating-panel-container bg-light cl-dark")}
   [:span "I found this conference ..."]
   [:div {:class "row"}
    [:div {:class "col-lg-12 col-md-12 col-sm-12 col-xs-12"}
     (checkboxes/checkbox-description-right :tags.inspiring "inspiring" "This conference had an impact on me.")
     (checkboxes/checkbox-description-right :tags.informative "informative" "I learned a lot during the workshops, sessions and talks.")
     (checkboxes/checkbox-description-right :tags.entertaining "entertaining" "The speakers where showmasters and the after party was amazing.")
     (checkboxes/checkbox-description-right :tags.hires "good to meet potential hires" "I met amazing people I would like to work with in the future.")
     (checkboxes/checkbox-description-right :tags.clients "good to meet potential clients" "The business sponsoring or being present at this conference coud be our next client.")]]])

(defn experience-panel []
  [:div {:class (str "panel rating-panel-container bg-light cl-dark")}
   [:span "I found this conference suitable for ..."]
   [:div {:class "row"}
    [:div {:class "col-lg-1 col-md-1 col-sm-1 hidden-xs"}]
    (checkboxes/experience-checkbox :experience.rookie "Rookie")
    (checkboxes/experience-checkbox :experience.beginner "Beginner")
    (checkboxes/experience-checkbox :experience.intermediate "Intermediate")
    (checkboxes/experience-checkbox :experience.advanced "Advanced")
    (checkboxes/experience-checkbox :experience.expert "Expert")
    [:div {:class "col-lg-1 col-md-1 col-sm-1 hidden-xs"}]]])

(defn comment-panel []
  [:div {:class (str "panel rating-panel-container bg-light cl-dark")}
   [:span "I want to say ..."]
   [:div {:class "form-group"}
    [:input {:field :text :id :comment.authorLabel :type "text" :read-only true :class "form-control"}]
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

(defn propagate-changes-in-user-info [doc]
  (add-watch user-info/user-info :watch-rating
             (fn [_ _ _ new]
               (swap! doc
                      (fn [d] (assoc-in d [:comment :authorLabel] (util/user-text new) ))))))

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
                   :comment     {:comment ""
                                 :authorLabel (util/user-text @user-info/user-info)}
                   :rating      {:overall    -1
                                 :talks      -1
                                 :venue      -1
                                 :networking -1}
                   :tags        {:inspiring    false
                                 :informative  false
                                 :entertaining false
                                 :hires        false
                                 :clients      false}})
        temporary-broken-nav-bar-empty-list []]
    (propagate-changes-in-user-info doc)
    [:div {:data-e2e "page-add-rating"}
     (navbar/nav-bar temporary-broken-nav-bar-empty-list)
     [:div {:class "container-fluid content-container pad-top"}
      [:div {:class "row add-rating-container"}
       [:div {:class "col-lg-1 col-md-1"}]
       [:div {:class "col-lg-10 col-md-9"}
        [forms/bind-fields add-rating-template doc]]
       [:div {:class "col-lg-1 col-md-2 col-sm-12 col-xs-12"}
        [:button {:class "btn btn-lg btn-orange button-add-rating" :on-click #(create-rating doc (session/get! :conference-id-to-rate)) :data-e2e "button-add-rating"} "add vote"]
        [:p {:class "cl-orange text-bold label-add-rating"} "Please note that you can only vote once for this conference!"]]]]]))

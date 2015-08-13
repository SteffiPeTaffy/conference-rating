(ns conference-rating.rating.add-rating
  (:require [reagent-forms.core :as forms]
            [reagent.core :refer [atom]]
            [ajax.core :as ajax]
            [conference-rating.panel :as panel]
            [conference-rating.form :as form]
            [reagent.session :as session]
            [conference-rating.history :as history]))

(defn create-rating [form-data conference-id]
    (ajax/POST (str "/api/conferences/" conference-id "/ratings") {:params        @form-data
                                                                   :format        :json
                                                                   :handler       (history/redirect-to (str "/conferences/" conference-id))
                                                                   :error-handler #(js/alert (str "could not create rating" %1))})
  (println @form-data))


(def add-rating-template
   [:div {:class "row"}
     [:div {:class "col-lg-4"}
      (panel/light-panel "Awesome" (form/form-block
                               [:div {:class "form-group"}
                                [:label {:class "form-checkbox form-icon btn btn-success active form-text"}
                                 [:input {:type "checkbox" }] "I would go again!"]]))
      (panel/light-panel "My name is" [:div {:class "form-group"}
                                 [:input {:type "text" :placeholder "author" :class "form-control"}]])
      (panel/light-panel "I want to say" [:div {:class "form-group"}
                                  [:textarea {:placeholder "author" :class "form-control" :rows 5}]]) ]
     [:div {:class "col-lg-4"}
      (panel/light-panel "This was"
                   (form/form-block
                     (form/checkbox "I found it inspiring")
                     (form/checkbox "I found it entertainning")
                     (form/checkbox "I learned a lot")
                     (form/checkbox "I met potential hires")
                     (form/checkbox "I met potential clients")))

      (panel/light-panel "This is interesting for"
                   (form/form-block
                     (form/checkbox "Devs")
                     (form/checkbox "QAs")
                     (form/checkbox "BAs")
                     (form/checkbox "Sales")
                     (form/checkbox "Recruiting")))]
     [:div {:class "col-lg-4"}
      (panel/light-panel "How was it"
                   [:div
                    (form/form-block
                      (form/radiobutton-group "Overall"))
                    (form/form-block
                     (form/radiobutton-group "Talks"))
                    (form/form-block
                      (form/radiobutton-group "Venue"))
                    (form/form-block
                      (form/radiobutton-group "Community"))])]])


(defn add-rating []
  (let [doc (atom {})]
    [:div {:class "container"}
     [forms/bind-fields add-rating-template doc]
     [:button {:class "btn btn-primary" :on-click #(create-rating doc (session/get! :conference-id-to-rate))} "add rating"]]))


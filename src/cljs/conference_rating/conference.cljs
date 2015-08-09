(ns conference-rating.conference
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :as forms]
            [reagent.session :as session]
            [ajax.core :as ajax]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]))

(defonce displayed-conference (atom nil))
(defonce conferences (atom nil))
(defonce ratings (atom nil))

;; -------------------------
;; Requests
(defn load-conference [id]
  (ajax/GET (str "/api/conferences/" id) {:handler #(reset! displayed-conference %1)
                                          :error-handler #(js/alert (str "conference not found" %1))
                                          :response-format :json
                                          :keywords? true}))

(defn load-conference-ratings [id]
  (ajax/GET (str "/api/conferences/" id "/ratings") {:handler #(reset! ratings %1)
                                          :error-handler #(js/alert (str "ratings not found" %1))
                                          :response-format :json
                                          :keywords? true}))

(defn load-conferences []
  (ajax/GET "/api/conferences" {:handler #(reset! conferences %1)
                                :error-handler #(js/alert (str "conferences not found" %1))
                                :response-format :json
                                :keywords? true}))


;; -------------------------
;; Views
(defn form-input [label input]
  [:div {:class "form-group"}
   [:label {:for (:id (second input))} label]
   input])

(defn display-loading []
  [:div [:h2 "Loading..."]])

(defn display-rating [conference-ratings-entry]
  [:div {:class "well" :key (:_id conference-ratings-entry)}
   [:div {:class "media"}
    [:div {:clas "media-body"}
     [:p {:class "text-right"} (str "By " (:author conference-ratings-entry))]
     [:p (str (:stars conference-ratings-entry) " out of 5 stars")]
     [:p (:comment conference-ratings-entry)]]]])

(defn display-ratings [conference-ratings]
  [:div (map display-rating conference-ratings)])

(def add-rating-template
  [:div
   (form-input "Author" [:input {:type "text" :placeholder "author" :class "form-control" :field :text :id :author}])
   (form-input "Stars" [:input {:type "number" :placeholder "stars" :class "form-control" :min 1 :max 5 :field :range :id :stars}])
   (form-input "Comment" [:textarea {:placeholder "comment" :class "form-control" :rows "5" :field :textarea :id :comment}])])

(defn create-rating [form-data]
  (let [conference @displayed-conference]
  (ajax/POST (str "/api/conferences/" (:_id conference) "/ratings") {:params @form-data
                                                                    :format :json
                                                                    :handler #(js/alert "success")
                                                                    :error-handler #(js/alert (str "could not create rating" %1))}))
    (println @form-data))

(defn display-conference [conference]
  (let [conference-ratings @ratings
        doc (atom {})]
  [:div {:class "container"}
   [:div {:class "jumbotron"}
    [:h1(:name conference)]
    [:p (:description conference)]]
   [:div {:class "panel panel-default"}
    [:div {:class "panel-heading"} "rate this conference now!"]
    [:div {:class "panel-body"}
      [forms/bind-fields add-rating-template doc]
      [:button {:class "btn btn-primary" :on-click #(create-rating doc)} "add rating"]]]
   [:div (display-ratings conference-ratings)]]))

(defn conference-page []
  (let [conference @displayed-conference]
    (if (not (nil? conference))
      (display-conference conference)
      (display-loading))))

(defn display-conference-list-item [conference-list-entry]
  [:div {:key (:_id conference-list-entry) :class "panel"}
   [:div {:class "panel-heading bg-purple cl-light"}
    [:h4 {:class "mar-no"} (:name conference-list-entry)]
    [:p "10.09.2015 - 13.09.2015"]]
   [:div {:class "panel-body"}
    [:div {:class "text-lg-right"}
     [:button {:class "btn-sm btn-primary"} "add rating"]]
    [:p {:class "text-muted"} (:description conference-list-entry)]
    [:a {:href (str "#/conferences/" (:_id conference-list-entry))} "go to conference overview"]]])

(defn display-conference-list [conference-list]
  [:div {:class "container"}
   [:h1 "Conferences"]
   [:div (map display-conference-list-item conference-list)]
   [:h2 "Actions"]
   [:ul
    [:li [:a {:href "#/add-conference"} "Add conference"]]]])

(defn conferences-page []
  (let [conference-list @conferences]
    (if (not (nil? conference-list))
      (display-conference-list conference-list)
      (display-loading))))


(def conference-form-template
  [:div
   (form-input "Name" [:input {:field :text :id :name :class "form-control" :placeholder "Name of the conference"}])
   (form-input "Date" [:input {:field :text :id :date :class "form-control" :placeholder "Date of the conference"}])
   (form-input "Link" [:input {:field :text :id :link :class "form-control" :placeholder "Link to the conference page"}])
   (form-input "Description" [:textarea {:field :textarea :rows 5 :id :description :class "form-control" :placeholder "More information about the conference"}])])

(defn create-conference [form-data]
  (ajax/POST "/api/conferences/" {:params @form-data
                                  :format :json
                                  :handler #(js/alert "success")
                                  :error-handler #(js/alert (str "could not create conference" %1))})
  (println @form-data))

(defn add-conference-page []
  (let [doc (atom {})]
    [:div {:class "container"}
     [:div {:class "panel panel-default"}
      [:div {:class "panel-heading"} "Add conference"]
      [:div {:class "panel-body"}
       [forms/bind-fields conference-form-template doc]
       [:div [:button {:class "btn btn-primary":on-click #(create-conference doc)} "Create"]]]]]))
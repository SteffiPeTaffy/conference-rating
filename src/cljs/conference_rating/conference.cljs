(ns conference-rating.conference
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :as forms]
            [reagent.session :as session]
            [ajax.core :as ajax]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]))

(def displayed-conference (atom nil))
(def conferences (atom nil))
(def ratings (atom nil))

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
(defn display-loading []
  [:div [:h2 "Loading..."]])

(defn display-rating [conference-ratings-entry]
  [:div {:class "well" :key (:id conference-ratings-entry)}
   [:div {:class "media"}
    [:div {:clas "media-body"}
     [:p {:class "text-right"} (str "By " (:rating-author conference-ratings-entry))]
     [:p (str (:rating-stars conference-ratings-entry) " out of 5 stars")]
     [:p (:rating-comment conference-ratings-entry)]]]])

(defn display-ratings [conference-ratings]
  [:div (map display-rating conference-ratings)])

(def add-rating-template
  [:div
   [:div {:class "form-group"}
    [:input {:type "text" :placeholder "author" :class "form-control" :field :text :id :author}]]
   [:div {:class "form-group"}
    [:input {:type "number" :placeholder "stars" :class "form-control" :min 1 :max 5 :field :range :id :stars}]]
   [:div {:class "form-group"}
    [:textarea {:placeholder "comment" :class "form-control" :rows "5" :field :textarea :id :comment}]]])

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
  [:li {:key (:_id conference-list-entry)}
   [:a {:href (str "#/conferences/" (:_id conference-list-entry))} (:name conference-list-entry)]])

(defn display-conference-list [conference-list]
  [:div {:class "container"}
   [:h1 "Conferences"]
   [:ul (map display-conference-list-item conference-list)]
   [:h2 "Actions"]
   [:ul
    [:li [:a {:href "#/add-conference"} "Add conference"]]]])

(defn conferences-page []
  (let [conference-list @conferences]
    (if (not (nil? conference-list))
      (display-conference-list conference-list)
      (display-loading))))


(defn row [label input]
  [:div.row
   [:div.col-md-2 [:label label]]
   [:div.col-md-5 input]])

(def conference-form-template
  [:div
   (row "Name" [:input {:field :text :id :name}])
   (row "Description" [:textarea {:field :textarea :id :description}])])

(defn create-conference [form-data]
  (ajax/POST "/api/conferences/" {:params @form-data
                                  :format :json
                                  :handler #(js/alert "success")
                                  :error-handler #(js/alert (str "could not create conference" %1))})
  (println @form-data))

(defn add-conference-page []
  (let [doc (atom {})]
    [:div
     [:h2 "Add conference"]
     [:div
      [forms/bind-fields conference-form-template doc]
      [:div [:button {:on-click #(create-conference doc)} "Create"]]]]))
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

(defn display-conference-ratings [conference-ratings]
  [:div (map display-rating conference-ratings)])

(defn display-conference [conference]
  (let [conference-ratings @ratings]
  [:div {:class "container"}
   [:div {:class "jumbotron"}
    [:h1(:conference-name conference)]
    [:p (:conference-description conference)]]
   [:div (display-conference-ratings conference-ratings)]]))

(defn conference-page []
  (let [conference @displayed-conference]
    (if (not (nil? conference))
      (display-conference conference)
      (display-loading))))

(defn display-conference-list-item [conference-list-entry]
  [:li {:key (:id conference-list-entry)}
   [:a {:href (str "#/conferences/" (:id conference-list-entry))} (:conference-name conference-list-entry)]])

(defn display-conference-list [conference-list]
  [:div {:class "container"}
   [:h1 "Conferences"]
   [:ul (map display-conference-list-item conference-list)]])

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
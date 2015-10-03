(ns conference-rating.view-utils.typeahead
  (:require [reagent.core :as reagent]))

(defn config [c]
  (clj->js c))

(defn wrap-source [source-fn]
  (fn [q cb]
    (source-fn q (fn [data]
                   (cb
                     (println "in wrapper" data (clj->js data))
                     (clj->js data))))))

(defn wrap-display [display-fn]
  (fn [suggestion]
    (display-fn (js->clj suggestion :keywordize-keys true))))

(defn wrap-template [[k template-fn]]
  [k (fn [suggestion]
       (template-fn (js->clj suggestion :keywordize-keys true)))])

(defn wrap-templates [templates]
  (->> templates
       (map wrap-template)
       (into {})))

(defn data-sets [d]
  (let [result (-> d
                   (assoc :source (wrap-source (:source d)))
                   (assoc :display (wrap-display (:display d)))
                   (assoc :templates (wrap-templates (:templates d))))]
    (clj->js result)))

(defn init-typeahead
  ([input-component config data-sets]
   (init-typeahead input-component config data-sets (fn [& _])))
  ([input-component config data-sets on-select]
    (with-meta input-component
               {:component-did-mount
                (fn [this]
                  (doto (js/jQuery (reagent/dom-node this))
                    (.typeahead config
                                data-sets)
                    (.bind "typeahead:select" (fn [e suggestion] (on-select e (js->clj suggestion :keywordize-keys true))))))})))
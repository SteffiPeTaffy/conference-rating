(ns conference-rating.view-utils.typeahead)

(defn config [c]
  (clj->js c))

(defn wrap-source [source-fn]
  (fn [q cb]
    (source-fn q (fn [data]
                   (cb
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
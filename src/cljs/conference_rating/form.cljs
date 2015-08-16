(ns conference-rating.form)

(defn checkbox [label]
  [:div label
   [:input {:type "checkbox" :id (str label "-checkbox-id")}]
   [:label {:for (str label "-checkbox-id")}
    [:span {:class "checkbox"}]]])

(defn radiobutton [label group-name]
  [:div label
   [:input {:type "radio" :id (str label "-radio-id") :name group-name}]
   [:label {:for (str label "-radio-id")}
    [:span {:class "radio"}]]])

(defn radiobutton-group [label]
  [:div {:key label}
    [:p label]
    [:div
      (radiobutton 1 label)
      (radiobutton 2 label)
      (radiobutton 3 label)
      (radiobutton 4 label)
      (radiobutton 5 label)]])

(defn form-block [& contents]
  [:div {:class "form-block"} contents]
  )
(ns conference-rating.form)

(defn checkbox [label]
  [:div {:class "checkbox" :key label}
   [:label {:class "form-checkbox form-normal form-primary active form-text"}
    [:input {:type "checkbox"} label]]]
  )

(defn radiobutton [label group-name]
  [:label {:class "form-radio form-normal form-text"}
   [:input {:type "radio" :name group-name} label]]
  )

(defn radiobutton-group [label]
  [:div {:key label}
    [:p label]
    [:div {:class "radio"}
      (radiobutton 1 label)
      (radiobutton 2 label)
      (radiobutton 3 label)
      (radiobutton 4 label)
      (radiobutton 5 label)]])

(defn form-block [& contents]
  [:div {:class "form-block"} contents]
  )
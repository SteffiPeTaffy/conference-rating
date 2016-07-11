(ns conference-rating.util
  (:require [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [clojure.string :as s]
            [goog.string :as gstring]
            [clojure.string :as str]))

(defn- value-true? [[_ v]]
  (true? v))

(defn checkboxes-to-tag-list [m]
  (->> m
       (filter value-true?)
       (map key)))


(defn display-loading []
  [:div [:h2 "Loading..."]])


(def built-in-formatter (tf/formatters :date-hour-minute-second-ms))

(defn assoc-when [map key value]
  (if value (assoc map key value) map))

(defn form-date-to-datestr [date]
  (tf/unparse built-in-formatter
              (t/date-time (:year date) (:month date) (:day date))))

(defn parse-string-to-date [date-str]
  (tf/parse built-in-formatter date-str))

(defn parse-date [date-str]
  (tf/parse built-in-formatter date-str))

(defn format-date [date-str format]
  (let [datetime (parse-date date-str)]
    (tf/unparse (tf/formatter format) datetime)))

(defn format-to-human-readable-date [date-str]
  (format-date date-str "dd MMMM YYYY"))

(defn format-date-to-calendar-format [date-str]
  (format-date date-str "yyyy/MM/dd"))

(defn from-to-dates [from-date to-date]
  (cond
    (and from-date from-date) [:p {:class "conference-dates" :data-e2e "text-conference-from-to-dates"} (str (format-to-human-readable-date from-date) " - " (format-to-human-readable-date to-date))]
    from-date [:p {:class "conference-dates"} (format-to-human-readable-date from-date)]
    :else [:p {:class "conference-dates"} "TBD"]))

(defn- add-nbsp [s]
  (if (= (s/trim s) "")
    (gstring/unescapeEntities "&nbsp;")
    s))

(def safe-character-mapping
  {"&quot;" "\""
   "&apos;" "'"
   "&lt;"   "<"
   "&gt;"   ">"
   "&amp;"  "&"})

(defn- unescape [s]
  (reduce (fn [x [escaped unescaped]] (str/replace x escaped unescaped)) s safe-character-mapping))

(defn- to-paragraph [s]
  [:p s])

(defn formatted-text [t]
  (if t
    (as-> t $
          (unescape $)
          (s/split $ #"\n")
          (map add-nbsp $)
          (map to-paragraph $))))

(defn link [link]
  [:p {:class "conference-link"}[:a {:href link :target "_blank" :class "conference-link" :data-e2e "text-conference-link"} link]])
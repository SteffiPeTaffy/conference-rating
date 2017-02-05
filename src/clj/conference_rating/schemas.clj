(ns conference-rating.schemas
  (:require [schema.core :as s]
            [schema.coerce :as coerce]))

(defn max-length [l] (s/pred (fn [x] (<= (count x) l)) (str "max length" l)))

(def Count s/Int)

(def RatingSummary
  {:avg s/Num :count Count})


(def AggregateRatings
  {:number-of-ratings Count
   :recommendations   Count
   :overall           RatingSummary
   :talks             RatingSummary
   :venue             RatingSummary
   :community         RatingSummary
   :roles             {:dev        Count
                       :devops     Count
                       :ux         Count
                       :qa         Count
                       :ba         Count
                       :pm         Count
                       :sales      Count
                       :recruiting Count
                       :other      Count}
   :experience        {:rookie       Count
                       :beginner     Count
                       :intermediate Count
                       :advanced     Count
                       :expert       Count}
   :tags              {:inspiring    Count
                       :informative  Count
                       :entertaining Count
                       :hires        Count
                       :clients      Count}})

(def Role (s/enum :dev :devops :qa :ux :pm :ba :sales :recruiting :other))
(def Experience (s/enum :rookie :beginner :intermediate :advanced :expert))
(def Tags (s/enum :inspiring :informative :entertaining :hires :clients))

(def RatingValue s/Int)                                     ; TODO: make this a range?

(def User
  {:email                      s/Str
   (s/optional-key :firstName) (s/maybe s/Str)
   (s/optional-key :lastName)  (s/maybe s/Str)})

(def Attendance
  {(s/optional-key :_id) s/Str
   :user                 User
   :conference-id        s/Str})

(def Rating
  {(s/optional-key :_id)  s/Str
   (s/optional-key :user) User
   :conference-id         s/Str
   :recommended           s/Bool
   :roles                 [Role]
   :experience            [Experience]
   :comment               {(s/optional-key :name) (s/both s/Str (max-length 100))
                           :comment               (s/both s/Str (max-length 10000))}
   :rating                {:overall    RatingValue
                           :talks      RatingValue
                           :venue      RatingValue
                           :networking RatingValue}
   :tags                  [Tags]})


(defn possible-values [enum]
  (:vs enum))

(def coerce-rating (coerce/coercer Rating coerce/json-coercion-matcher))

(def coerce-attendance (coerce/coercer Attendance coerce/json-coercion-matcher))


(def LocationSchema ; Location is already taken...
  {:place-id s/Str
   :name     s/Str
   :address  s/Str
   :lat      s/Num
   :lng      s/Num})

(def Conference
  {(s/optional-key :_id)      s/Str
   :series                    (s/both s/Str (max-length 100))
   :name                      (s/both s/Str (max-length 100))
   :from                      s/Str
   :to                        s/Str
   :link                      (s/both s/Str (max-length 1000))
   :description               (s/both s/Str (max-length 10000))
   (s/optional-key :location) LocationSchema
   (s/optional-key :attendees) [User]})
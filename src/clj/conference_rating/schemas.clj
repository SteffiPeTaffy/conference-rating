(ns conference-rating.schemas
  (:require [schema.core :as s]))

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
   :roles             {
                       :dev       Count
                       :devops    Count
                       :ux        Count
                       :qa        Count
                       :ba        Count
                       :pm        Count
                       :sales     Count
                       :recruting Count
                       :other     Count}
   :experience        {
                       :rookie       Count
                       :beginner     Count
                       :intermediate Count
                       :advanced     Count
                       :expert       Count}
   :tags              {
                       :inspiring         Count
                       :entertaining      Count
                       :learning          Count
                       :potential-hires   Count
                       :potential-clients Count}})

(def Role (s/enum :dev :devops :qa :ux :pm :ba :sales :recruiting :other))
(def Experience (s/enum :rookie :beginner :intermediate :advanced :expert))
(def Tags (s/enum :inspiring :informative :entertaining :hires :clients))

(def RatingValue s/Int) ; TODO: make this a range?

(def Rating
  {(s/optional-key :_id) s/Str
   :conference-id        s/Str
   :recommended          s/Bool
   :roles                [Role]
   :experience           [Experience]
   :comment              {:name    s/Str
                          :comment s/Str}
   :rating               {:overall    RatingValue
                          :talks      RatingValue
                          :venue      RatingValue
                          :networking RatingValue}
   :tags                 [Tags]})
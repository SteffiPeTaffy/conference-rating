(ns conference-rating.aggregator)

(defn aggregate-ratings [ratings]
  {:number-of-ratings (count ratings)
   :recommendations   1445
   :overall           {:avg 4 :count 8}
   :talks             {:avg 0.1 :count 7}
   :venue             {:avg 3 :count 6}
   :community         {:avg 2.5 :count 5}
   :roles             {
                       :dev       1
                       :devops    2
                       :ux        3
                       :qa        4
                       :ba        5
                       :pm        6
                       :sales     7
                       :recruting 8
                       :other     9}
   :experience        {
                       :rookie       1
                       :beginner     3
                       :intermediate 12
                       :advanced     5
                       :expert       0}
   :tags              {
                       :inspiring         2
                       :entertaining      2
                       :learning          2
                       :potential-hires   1
                       :potential-clients 1}})
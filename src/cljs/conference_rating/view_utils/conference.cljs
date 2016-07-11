(ns conference-rating.view-utils.conference
  (:require [conference-rating.util :as util]
            [cljs-time.core :as t]))

(defn is-future-conference? [conference]
  (t/after? (util/parse-string-to-date (:from conference)) (t/now)))

(defn- ratings-key-for [conference]
  (if (is-future-conference? conference)
    :average-series-rating
    :aggregated-ratings))

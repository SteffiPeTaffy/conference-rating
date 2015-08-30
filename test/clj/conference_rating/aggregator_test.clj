(ns clj.conference-rating.aggregator-test
  (:use [conference-rating.aggregator])
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [schema.test]
            [conference-rating.testdata :refer [some-rating-with some-rating]]
            [schema.core :as s]))

(use-fixtures :once schema.test/validate-schemas)

(deftest aggregator-test
  (testing "number of ratings"
    (is (= 3 (:number-of-ratings (aggregate-ratings [(some-rating)
                                                     (some-rating)
                                                     (some-rating)]))))))
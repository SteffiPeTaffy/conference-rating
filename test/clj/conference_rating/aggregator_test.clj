(ns clj.conference-rating.aggregator-test
  (:use [conference-rating.aggregator])
  (:require [clojure.test :refer [deftest is testing]]
            [conference-rating.testdata :refer [some-rating-with some-rating]]))

(deftest aggregator-test
  (testing "number of ratings"
    (is (= 3 (:number-of-ratings (aggregate-ratings [(some-rating)
                                                     (some-rating)
                                                     (some-rating)]))))))
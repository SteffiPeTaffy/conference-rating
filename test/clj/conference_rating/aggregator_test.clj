(ns clj.conference-rating.aggregator-test
  (:use [conference-rating.aggregator])
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [schema.test]
            [conference-rating.testdata :refer [some-rating-with some-rating some-rating-values-with]]
            [schema.core :as s]))

(use-fixtures :once schema.test/validate-schemas)

(deftest aggregator-test
  (testing "number of ratings"
    (is (= 3 (:number-of-ratings (aggregate-ratings [(some-rating)
                                                     (some-rating)
                                                     (some-rating)])))))
  (testing "number of recommendations"
    (is (= 2 (:recommendations (aggregate-ratings [(some-rating-with :recommended true)
                                                   (some-rating-with :recommended false)
                                                   (some-rating-with :recommended true)])))))
  (testing "overall rating"
    (is (= {:avg 11/3 :count 3} (:overall (aggregate-ratings [(some-rating-with
                                                                :rating (some-rating-values-with :overall 5))
                                                              (some-rating-with
                                                                :rating (some-rating-values-with :overall 4))
                                                              (some-rating-with
                                                                :rating (some-rating-values-with :overall 2))]))))
    (is (= {:avg 7/2 :count 2} (:overall (aggregate-ratings [(some-rating-with
                                                               :rating (some-rating-values-with :overall 5))
                                                             (some-rating-with
                                                               :rating (some-rating-values-with :overall -1))
                                                             (some-rating-with
                                                               :rating (some-rating-values-with :overall 2))]))))
    (is (= {:avg 0 :count 0} (:overall (aggregate-ratings [])))))
  (testing "talks rating"
    (is (= {:avg 11/3 :count 3} (:talks (aggregate-ratings [(some-rating-with
                                                              :rating (some-rating-values-with :talks 5))
                                                            (some-rating-with
                                                              :rating (some-rating-values-with :talks 4))
                                                            (some-rating-with
                                                              :rating (some-rating-values-with :talks 2))]))))
    (is (= {:avg 7/2 :count 2} (:talks (aggregate-ratings [(some-rating-with
                                                             :rating (some-rating-values-with :talks 5))
                                                           (some-rating-with
                                                             :rating (some-rating-values-with :talks -1))
                                                           (some-rating-with
                                                             :rating (some-rating-values-with :talks 2))]))))
    (is (= {:avg 0 :count 0} (:talks (aggregate-ratings [])))))
  (testing "venue rating"
    (is (= {:avg 11/3 :count 3} (:venue (aggregate-ratings [(some-rating-with
                                                              :rating (some-rating-values-with :venue 5))
                                                            (some-rating-with
                                                              :rating (some-rating-values-with :venue 4))
                                                            (some-rating-with
                                                              :rating (some-rating-values-with :venue 2))]))))
    (is (= {:avg 7/2 :count 2} (:venue (aggregate-ratings [(some-rating-with
                                                             :rating (some-rating-values-with :venue 5))
                                                           (some-rating-with
                                                             :rating (some-rating-values-with :venue -1))
                                                           (some-rating-with
                                                             :rating (some-rating-values-with :venue 2))]))))
    (is (= {:avg 0 :count 0} (:venue (aggregate-ratings [])))))
  (testing "community rating"
    (is (= {:avg 11/3 :count 3} (:community (aggregate-ratings [(some-rating-with
                                                                  :rating (some-rating-values-with :networking 5))
                                                                (some-rating-with
                                                                  :rating (some-rating-values-with :networking 4))
                                                                (some-rating-with
                                                                  :rating (some-rating-values-with :networking 2))]))))
    (is (= {:avg 7/2 :count 2} (:community (aggregate-ratings [(some-rating-with
                                                                 :rating (some-rating-values-with :networking 5))
                                                               (some-rating-with
                                                                 :rating (some-rating-values-with :networking -1))
                                                               (some-rating-with
                                                                 :rating (some-rating-values-with :networking 2))]))))
    (is (= {:avg 0 :count 0} (:community (aggregate-ratings [])))))
  (testing "roles"
    (is (= {:dev        2
            :devops     2
            :ux         1
            :qa         0
            :ba         0
            :pm         0
            :sales      0
            :recruiting 0
            :other      0} (:roles (aggregate-ratings
                                     [(some-rating-with :roles [:dev :devops])
                                      (some-rating-with :roles [:devops :dev])
                                      (some-rating-with :roles [:ux])])))))
  (testing "experience"
    (is (= {:rookie       1
            :beginner     0
            :intermediate 2
            :advanced     1
            :expert       0} (:experience (aggregate-ratings
                                            [(some-rating-with :experience [:intermediate :advanced])
                                             (some-rating-with :experience [:intermediate])
                                             (some-rating-with :experience [:rookie])])))))
  (testing "tags"
    (is (= {:inspiring    0
            :entertaining 0
            :informative  0
            :hires        0
            :clients      1} (:tags (aggregate-ratings
                                      [(some-rating-with :tags [:clients])]))))))
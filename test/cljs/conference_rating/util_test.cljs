(ns conference-rating.util-test
  (:require [cemerick.cljs.test :refer-macros [is are deftest testing use-fixtures done]]
            [reagent.core :as reagent :refer [atom]]
            [conference-rating.util :as u]))

(deftest checkboxes-to-tag-list-test
         (testing "that it converts a map of keys and boolean values into a list of keys that are true"
                  (is (= [:foo :bar] (u/checkboxes-to-tag-list {:foo true
                                                              :baz false
                                                              :bar true})))))

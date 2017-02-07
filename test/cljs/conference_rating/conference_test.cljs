(ns conference-rating.conference-test
  (:require [cemerick.cljs.test :refer-macros [is are deftest testing use-fixtures done]]
            [conference-rating.view-utils.conference :as c]))



(deftest conference-attending-label-text
         (testing "should provide correct label for past conference with no attendees"
                  (is (= "No one was here." (c/attending-label {:from      "2016-02-06T16:31:03.679"
                                                                :to        "2016-02-06T16:31:03.679"
                                                                :attendees []}))))
         (testing "should provide correct label for past conference with one other attendee"
                  (is (= "1 others were here." (c/attending-label {:from      "2016-02-06T16:31:03.679"
                                                                   :to        "2016-02-06T16:31:03.679"
                                                                   :attendees [{:email "notmy@email.com"}]}))))
         (testing "should provide correct label for past conference with two others attendee"
                  (is (= "2 others were here." (c/attending-label {:from      "2016-02-06T16:31:03.679"
                                                                   :to        "2016-02-06T16:31:03.679"
                                                                   :attendees [{:email "notmy@email.com"} {:email "alsonotmy@email.com"}]}))))
         (testing "should provide correct label for futre conference with one other attendee"
                  (is (= "No one is going, yet." (c/attending-label {:from      "2111-02-06T16:31:03.679"
                                                                     :to        "2111-02-06T16:31:03.679"
                                                                     :attendees []}))))
         (testing "should provide correct label for futre conference with one other attendee"
                  (is (= "1 others are going." (c/attending-label {:from      "2111-02-06T16:31:03.679"
                                                                   :to        "2111-02-06T16:31:03.679"
                                                                   :attendees [{:email "notmy@email.com"}]}))))
         (testing "should provide correct label for futre conference with one other attendee"
                  (is (= "2 others are going." (c/attending-label {:from      "2111-02-06T16:31:03.679"
                                                                   :to        "2111-02-06T16:31:03.679"
                                                                   :attendees [{:email "notmy@email.com"} {:email "alsonotmy@email.com"}]})))))

(deftest conference-attending-summary-label-tootltip
         (testing "should have tooltip text with attendees email"
                  (is (= "notmy@email.com\nalsonotmy@email.com" (c/conference-attendees-tooltip {:from         "2016-02-06T16:31:03.679"
                                                                                                    :to        "2016-02-06T16:31:03.679"
                                                                                                    :attendees [{:email "notmy@email.com"} {:email "alsonotmy@email.com"}]})))))

(deftest conference-voicers-button-tooltip
         (testing "should have tooltip text with voicers email"
                  (is (= "notmy@email.com\nalsonotmy@email.com" (c/conference-attendees-tooltip {:from         "2016-02-06T16:31:03.679"
                                                                                                 :to        "2016-02-06T16:31:03.679"
                                                                                                 :attendees [{:email "notmy@email.com"} {:email "alsonotmy@email.com"}]})))))
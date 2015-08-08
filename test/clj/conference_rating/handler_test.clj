(ns clj.conference-rating.handler-test
  (:use [conference-rating.handler])
  (:require [clojure.test :refer [deftest is testing]]
            [ring.mock.request :refer [request]]))

(deftest get-conference-test
  (testing "that it returns a conference"
    (is (= {:conference-name "Some Conference" :id "some-id"}
           (:body (get-conference "some-id"))))))

(deftest acceptance-test
  (testing "should have an index-page"
    (is (= 200 (:status (app (request :get "/"))))))
  (testing "should return a conference for an id as json"
    (is (= 200 (:status (app (request :get "/api/conferences/foo")))))
    (is (= "{\"conference-name\":\"Some Conference\",\"id\":\"foo\"}"
           (:body (app (request :get "/api/conferences/foo")))))))

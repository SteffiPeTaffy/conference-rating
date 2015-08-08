(ns clj.conference-rating.handler-test
  (:use [conference-rating.handler])
  (:require [clojure.test :refer [deftest is testing]]
            [ring.mock.request :refer [request]]
            [clojure.data.json :as json]))

(defn json-body-for [request] (json/read-str (:body (app request)) :key-fn keyword))

(deftest acceptance-test
  (testing "should have an index-page"
    (is (= 200 (:status (app (request :get "/"))))))
  (testing "should return a conference for an id as json"
    (is (= 200 (:status (app (request :get "/api/conferences/foo")))))
    (is (= {:conference-name "Some Conference"
            :conference-description "Some Conference Description"
            :id "foo"}
           (json-body-for (request :get "/api/conferences/foo")))))
  (testing "should return a list of conferences as json"
    (is (= 200 (:status (app (request :get "/api/conferences")))))
    (is (= [{:conference-name "Conference 1" :id "1"}
            {:conference-name "Conference 2" :id "2"}] (json-body-for (request :get "/api/conferences"))))))
(ns conference-rating.backend_test
  (:require [cemerick.cljs.test :refer-macros [is are deftest testing use-fixtures done]]
            [conference-rating.backend :as backend]))

(defn just-return-it [response] response)

(def sanitized-get-conference-response {:description "&lt;tag&gt;",
                                        :series      "test &amp; test",
                                        :name        "/ / &amp; &amp;",
                                        :location    {
                                                      :lng     100.532159,
                                                      :lat     13.7455534,
                                                      :address "236/8-9 ซอย สยามสแควร์ 2 Khwaeng Pathum Wan, Khet Pathum Wan, Krung Thep Maha Nakhon 10330, Tailandia",
                                                      :name    "Growth cafe &amp; co."}
                                        :link        "www.com.com"})

(defn mock-get-conference [endpoint request]
  ((:handler request) sanitized-get-conference-response))

(def sanitized-get-conferences-response {:total-items 3
                                         :items       [{:description "&lt;tag&gt;",
                                                        :series      "test &amp; test",
                                                        :name        "/ / &amp; &amp;",
                                                        :location    {
                                                                      :lng     100.532159,
                                                                      :lat     13.7455534,
                                                                      :address "236/8-9 ซอย สยามสแควร์ 2 Khwaeng Pathum Wan, Khet Pathum Wan, Krung Thep Maha Nakhon 10330, Tailandia",
                                                                      :name    "Growth cafe &amp; co."}
                                                        :link        "www.com.com"}
                                                       {:description "second &lt;",
                                                        :series      "test &amp; test",
                                                        :name        "second &amp;",
                                                        :location    {
                                                                      :lng     100.532159,
                                                                      :lat     13.7455534,
                                                                      :address "236/8-9 ซอย สยามสแควร์ 2 Khwaeng Pathum Wan, Khet Pathum Wan, Krung Thep Maha Nakhon 10330, Tailandia",
                                                                      :name    "Growth cafe &amp; co."}
                                                        :link        "www.com.com"}
                                                       {:description "third &lt; legacy conference without location",
                                                        :series      "test &amp; test",
                                                        :name        "third &amp;",
                                                        :link        "www.com.com"}]})

(defn mock-get-conferences [endpoint request]
  ((:handler request) sanitized-get-conferences-response))

(deftest load-one-conference
         (testing "should return unsanitized fields for a conference"
                  (let [response (backend/ajaxless-load-conference "id" just-return-it mock-get-conference)]
                    (is (= "<tag>" (:description response)))
                    (is (= "test & test" (:series response)))
                    (is (= "Growth cafe & co." (:name (:location response))))
                    (is (= "/ / & &" (:name response))))))

(deftest load-several-conferences
         (testing "should return unsanitized fields for several conferences"
                  (let [success-handler-args (atom nil)
                        mocked-success-handler (fn [conferences count-conferences] (reset! success-handler-args [conferences count-conferences]) conferences)
                        response (into [] (backend/ajaxless-load-conferences mocked-success-handler "/api/conferences" mock-get-conferences))]
                    (is (= 3 (nth @success-handler-args 1)))
                    (let [first-conf (get response 0)]
                      (is (= "<tag>" (:description first-conf)))
                      (is (= "test & test" (:series first-conf)))
                      (is (= "Growth cafe & co." (:name (:location first-conf))))
                      (is (= "/ / & &" (:name first-conf))))
                    (let [second-conf (get response 1)]
                      (is (= "second <" (:description second-conf)))
                      (is (= "test & test" (:series second-conf)))
                      (is (= "Growth cafe & co." (:name (:location second-conf))))
                      (is (= "second &" (:name second-conf))))
                    (let [third-conf (get response 2)]
                      (is (= "third < legacy conference without location" (:description third-conf)))
                      (is (= "test & test" (:series third-conf)))
                      (is (nil? (:location third-conf)))
                      (is (= "third &" (:name third-conf)))))))



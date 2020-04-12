(ns conference-rating.backend_test
  (:require [cemerick.cljs.test :refer-macros [is are deftest testing use-fixtures done]]
            [conference-rating.backend :as backend]))

(defn just-return-it [returned-conference] returned-conference)

(def sanitized-get-conference-response {:description "&lt;tag&gt;",
                         :series "test &amp; test",
                         :name  "/ / &amp; &amp;",
                         :location {
                               :lng 100.532159,
                               :lat 13.7455534,
                               :address "236/8-9 ซอย สยามสแควร์ 2 Khwaeng Pathum Wan, Khet Pathum Wan, Krung Thep Maha Nakhon 10330, Tailandia",
                               :name "Growth cafe &amp; co."}
                         :link "www.com.com"})

(defn mock-get-conference [endpoint request]
  ((:handler request) sanitized-get-conference-response))

(def sanitized-get-conferences-response [{:description "&lt;tag&gt;",
                                        :series "test &amp; test",
                                        :name  "/ / &amp; &amp;",
                                        :location {
                                                   :lng 100.532159,
                                                   :lat 13.7455534,
                                                   :address "236/8-9 ซอย สยามสแควร์ 2 Khwaeng Pathum Wan, Khet Pathum Wan, Krung Thep Maha Nakhon 10330, Tailandia",
                                                   :name "Growth cafe &amp; co."}
                                        :link "www.com.com"}
                                         {:description "second &lt;",
                                          :series "test &amp; test",
                                          :name  "second &amp;",
                                          :location {
                                                     :lng 100.532159,
                                                     :lat 13.7455534,
                                                     :address "236/8-9 ซอย สยามสแควร์ 2 Khwaeng Pathum Wan, Khet Pathum Wan, Krung Thep Maha Nakhon 10330, Tailandia",
                                                     :name "Growth cafe &amp; co."}
                                          :link "www.com.com"}
                                         {:description "third &lt; legacy conference without location",
                                          :series "test &amp; test",
                                          :name  "third &amp;",
                                          :link "www.com.com"}])

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
                  (let [response (into [] (backend/ajaxless-load-conferences just-return-it mock-get-conferences))]
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



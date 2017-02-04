(ns conference-rating.user-info
  (:require [reagent.core :refer [atom]]
            [conference-rating.backend :as backend]))

(def user-info (atom {}))

(defn load-user-info! []
  (backend/load-user-info #(reset! user-info %1)))

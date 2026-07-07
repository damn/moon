(ns clojure.get-data
  (:require [clojure.group :as group]
            [clojure.actor :as actor]))

(defn f
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (group/find-actor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (actor/get-user-object group)}))

(ns clojure.action-bar.get-data
  (:require
            [clojure.get-user-object] [clojure.group :as group]))

(defn f
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (group/find-actor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (clojure.get-user-object/f group)}))

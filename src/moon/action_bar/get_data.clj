(ns moon.action-bar.get-data
  (:require [gdl.get-user-object :refer [get-user-object]]
            [gdl.find-actor :refer [find-actor]]))

(defn f
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (find-actor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (get-user-object group)}))

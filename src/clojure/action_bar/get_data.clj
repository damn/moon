(ns clojure.action-bar.get-data
  (:require
            [clojure.scene2d.actor.get-user-object] [clojure.scene2d.group :as group]))

(defn f
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (group/find-actor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (clojure.scene2d.actor.get-user-object/f group)}))

(ns moon.action-bar.get-data
  (:require [clojure.gdx.actor.get-user-object :as get-user-object]
            [clojure.gdx.group.find-actor :as find-actor]))

(defn f
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (find-actor/f action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (get-user-object/f group)}))

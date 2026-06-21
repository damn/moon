(ns moon.action-bar.get-data
  (:require [com.badlogic.gdx.scenes.scene2d.actor.get-user-object :refer [get-user-object]]
            [com.badlogic.gdx.scenes.scene2d.group.find-actor :refer [find-actor]]))

(defn f
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (find-actor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (get-user-object group)}))

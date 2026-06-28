(ns moon.action-bar.get-data
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn f
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (Group/.findActor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (Actor/.getUserObject group)}))

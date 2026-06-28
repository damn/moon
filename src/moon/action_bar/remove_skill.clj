(ns moon.action-bar.remove-skill
  (:require [moon.action-bar.get-data :as get-data])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ButtonGroup)))

(defn f [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data/f action-bar)
        button (get horizontal-group skill-id)]
    (Actor/.remove button)
    (ButtonGroup/.remove button-group button)
    nil))

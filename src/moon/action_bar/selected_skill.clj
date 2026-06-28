(ns moon.action-bar.selected-skill
  (:require [moon.action-bar.get-data :as get-data])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ButtonGroup)))

(defn f [action-bar]
  (when-let [skill-button (ButtonGroup/.getChecked (:button-group (get-data/f action-bar)))]
    (Actor/.getUserObject skill-button)))

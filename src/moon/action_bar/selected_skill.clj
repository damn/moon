(ns moon.action-bar.selected-skill
  (:require [clojure.gdx.actor.get-user-object :as get-user-object]
            [moon.action-bar.get-data :as get-data])
  (:import (com.badlogic.gdx.scenes.scene2d.ui ButtonGroup)))

(defn f [action-bar]
  (when-let [skill-button (ButtonGroup/.getChecked (:button-group (get-data/f action-bar)))]
    (get-user-object/f skill-button)))

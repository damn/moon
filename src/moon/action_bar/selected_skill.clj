(ns moon.action-bar.selected-skill
  (:require [com.badlogic.gdx.scenes.scene2d.actor.get-user-object :refer [get-user-object]]
            [com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]
            [moon.action-bar.get-data :as get-data]))

(defn f [action-bar]
  (when-let [skill-button (button-group/checked (:button-group (get-data/f action-bar)))]
    (get-user-object skill-button)))

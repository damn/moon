(ns moon.action-bar.selected-skill
  (:require [scene2d.actor.get-user-object :refer [get-user-object]]
            [scene2d.ui.button-group.get-checked :as get-checked]
            [moon.action-bar.get-data :as get-data]))

(defn f [action-bar]
  (when-let [skill-button (get-checked/f (:button-group (get-data/f action-bar)))]
    (get-user-object skill-button)))

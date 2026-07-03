(ns moon.action-bar.selected-skill
  (:require [clojure.gdx.actor.get-user-object :as get-user-object]
            [clojure.gdx.button-group.get-checked :as get-checked]
            [moon.action-bar.get-data :as get-data]))

(defn f [action-bar]
  (when-let [skill-button (get-checked/f (:button-group (get-data/f action-bar)))]
    (get-user-object/f skill-button)))

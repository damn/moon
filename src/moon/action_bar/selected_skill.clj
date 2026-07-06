(ns moon.action-bar.selected-skill
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.button-group.get-checked :as get-checked]
            [moon.action-bar.get-data :as get-data]))

(defn f [action-bar]
  (when-let [skill-button (get-checked/f (:button-group (get-data/f action-bar)))]
    (actor/get-user-object skill-button)))

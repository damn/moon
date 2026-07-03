(ns moon.action-bar.remove-skill
  (:require [clojure.gdx.actor.remove :as remove]
            [clojure.gdx.button-group.remove :as button-group-remove]
            [moon.action-bar.get-data :as get-data]))

(defn f [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data/f action-bar)
        button (get horizontal-group skill-id)]
    (remove/f button)
    (button-group-remove/f button-group button)
    nil))

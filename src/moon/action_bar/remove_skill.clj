(ns moon.action-bar.remove-skill
  (:require [scene2d.actor.remove :refer [remove!]]
            [scene2d.ui.button-group.remove :as remove]
            [moon.action-bar.get-data :as get-data]))

(defn f [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data/f action-bar)
        button (get horizontal-group skill-id)]
    (remove! button)
    (remove/f! button-group button)
    nil))

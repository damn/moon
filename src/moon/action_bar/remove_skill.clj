(ns moon.action-bar.remove-skill
  (:require [scene2d.actor.remove :refer [remove!]]
            [ui.button-group :as button-group]
            [moon.action-bar.get-data :as get-data]))

(defn f [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data/f action-bar)
        button (get horizontal-group skill-id)]
    (remove! button)
    (button-group/remove! button-group button)
    nil))

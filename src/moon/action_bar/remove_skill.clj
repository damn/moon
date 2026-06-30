(ns moon.action-bar.remove-skill
  (:require [clojure.gdx :as gdx]
            [moon.action-bar.get-data :as get-data]))

(defn f [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data/f action-bar)
        button (get horizontal-group skill-id)]
    (gdx/remove! button)
    (gdx/button-group-remove! button-group button)
    nil))

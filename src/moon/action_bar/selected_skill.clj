(ns moon.action-bar.selected-skill
  (:require [clojure.gdx :as gdx]
            [moon.action-bar.get-data :as get-data]))

(defn f [action-bar]
  (when-let [skill-button (gdx/button-group-get-checked (:button-group (get-data/f action-bar)))]
    (gdx/get-user-object skill-button)))

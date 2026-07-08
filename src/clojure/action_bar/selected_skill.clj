(ns clojure.action-bar.selected-skill
  (:require
            [clojure.actor.get-user-object] [clojure.button-group :as button-group]
            [clojure.action-bar.get-data :as get-data]))

(defn f [action-bar]
  (when-let [skill-button (button-group/get-checked (:button-group (get-data/f action-bar)))]
    (clojure.actor.get-user-object/f skill-button)))

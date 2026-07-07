(ns moon.action-bar.selected-skill
  (:require [clojure.button-group :as button-group]
            [clojure.actor :as actor]
            [moon.action-bar.get-data :as get-data]))

(defn f [action-bar]
  (when-let [skill-button (button-group/get-checked (:button-group (get-data/f action-bar)))]
    (actor/get-user-object skill-button)))

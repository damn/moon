(ns clojure.action-bar.selected-skill
  (:require
            [clojure.get-user-object] [clojure.button-group :as button-group]
            [clojure.action-bar.get-data :as get-data]))

(defn f [action-bar]
  (when-let [skill-button (button-group/get-checked (:button-group (get-data/f action-bar)))]
    (clojure.get-user-object/f skill-button)))

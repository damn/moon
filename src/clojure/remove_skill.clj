(ns clojure.remove-skill
  (:require [clojure.button-group :as button-group]
            [clojure.actor :as actor]
            [clojure.get-data :as get-data]))

(defn f [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data/f action-bar)
        button (get horizontal-group skill-id)]
    (actor/remove! button)
    (button-group/remove! button-group button)
    nil))

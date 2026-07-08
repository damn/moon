(ns clojure.action-bar.remove-skill
  (:require
            [clojure.actor.remove-actor] [clojure.button-group :as button-group]
            [clojure.action-bar.get-data :as get-data]))

(defn f [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data/f action-bar)
        button (get horizontal-group skill-id)]
    (clojure.actor.remove-actor/f button)
    (button-group/remove! button-group button)
    nil))

(ns clojure.ui.action-bar.selected-skill
  (:require
            [gdl.scenes.scene2d.actor :as actor] [clojure.ui.button-group :as button-group]
            [clojure.ui.action-bar.get-data :as get-data]))

(defn f [action-bar]
  (when-let [skill-button (button-group/get-checked (:button-group (get-data/f action-bar)))]
    (actor/get-user-object skill-button)))

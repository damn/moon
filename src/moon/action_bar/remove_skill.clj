(ns moon.action-bar.remove-skill
  (:require
            [com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [moon.action-bar.get-data :as get-data]))

(defn f [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data/f action-bar)
        button (get horizontal-group skill-id)]
    (actor/remove! button)
    (button-group/remove! button-group button)
    nil))

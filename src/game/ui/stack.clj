(ns game.ui.stack
  (:require [com.badlogic.gdx.scenes.scene2d.ui.stack :as stack]
            [gdl.scene2d.actor :as actor]
            [moon.ui.group :as group]))

(defmethod actor/create :ui/stack
  [opts]
  (doto (stack/create)
    (group/set-opts! opts)))

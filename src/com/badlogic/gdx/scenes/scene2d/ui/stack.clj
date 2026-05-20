(ns com.badlogic.gdx.scenes.scene2d.ui.stack
  (:require [gdl.scene2d.actor :as actor]
            [gdl.scene2d.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Stack)))

(defmethod actor/create :ui/stack
  [opts]
  (doto (Stack.)
    (group/set-opts! opts)))

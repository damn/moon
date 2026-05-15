(ns com.badlogic.gdx.scenes.scene2d.ui.stack
  (:require [moon.ui.actor :as actor]
            [moon.ui.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Stack)))

(defmethod actor/create :ui/stack
  [opts]
  (doto (Stack.)
    (group/set-opts! opts)))

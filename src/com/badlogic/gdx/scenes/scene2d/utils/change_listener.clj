(ns com.badlogic.gdx.scenes.scene2d.utils.change-listener
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defmethod actor/create-listener
  :listener/change
  [[_ f]]
  (proxy [ChangeListener] []
    (changed [event actor]
      (f event actor))))

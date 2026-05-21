(ns com.badlogic.gdx.scenes.scene2d.utils.change-listener
  (:require [gdl.scene2d.listener :as listener])
  (:import (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defmethod listener/create
  :listener/change
  [[_ f]]
  (proxy [ChangeListener] []
    (changed [event actor]
      (f event actor))))

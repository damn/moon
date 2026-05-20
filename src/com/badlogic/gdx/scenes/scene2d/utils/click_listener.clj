(ns com.badlogic.gdx.scenes.scene2d.utils.click-listener
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.utils ClickListener)))

(defmethod actor/create-listener
  :listener/click
  [[_ f]]
  (proxy [ClickListener] []
    (clicked [event x y]
      (f event x y))))

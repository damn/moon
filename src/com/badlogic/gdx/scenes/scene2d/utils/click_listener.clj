(ns com.badlogic.gdx.scenes.scene2d.utils.click-listener
  (:require [clojure.scene2d.listener :as listener])
  (:import (com.badlogic.gdx.scenes.scene2d.utils ClickListener)))

(defmethod listener/create
  :listener/click
  [[_ f]]
  (proxy [ClickListener] []
    (clicked [event x y]
      (f event x y))))

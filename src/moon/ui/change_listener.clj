(ns moon.ui.change-listener ; integarte
  (:import (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn create [f]
  (proxy [ChangeListener] []
    (changed [event actor]
      (f event actor))))

(ns com.badlogic.gdx.scenes.scene2d.utils.change-listener
  (:import (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn proxy-change-listener [f]
  (proxy [ChangeListener] []
    (changed [event actor]
      (f event actor))))

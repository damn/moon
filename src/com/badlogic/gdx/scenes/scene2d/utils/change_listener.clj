(ns com.badlogic.gdx.scenes.scene2d.utils.change-listener
  (:import (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn create [changed-fn]
  (proxy [ChangeListener] []
    (changed [event actor]
      (changed-fn event actor))))

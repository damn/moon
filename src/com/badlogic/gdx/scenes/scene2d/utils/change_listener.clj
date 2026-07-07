(ns com.badlogic.gdx.scenes.scene2d.utils.change-listener
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn new [changed-fn]
  (proxy [ChangeListener] []
    (changed [event actor]
      (changed-fn event actor))))

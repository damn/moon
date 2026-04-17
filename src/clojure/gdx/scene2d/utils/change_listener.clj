(ns clojure.gdx.scene2d.utils.change-listener
  (:import (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn create [f]
  (proxy [ChangeListener] []
    (changed [event actor]
      (f event actor))))

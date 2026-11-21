(ns clojure.com.badlogic.gdx.scenes.scene2d.utils.change-listener
  (:import (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn create [changed!]
  (proxy [ChangeListener] []
    (changed [event actor]
      (changed! event actor))))

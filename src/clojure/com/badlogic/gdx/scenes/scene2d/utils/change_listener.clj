(ns clojure.com.badlogic.gdx.scenes.scene2d.utils.change-listener
  "Listener for change event."
  (:import (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn create [changed!]
  (proxy [ChangeListener] []
    (changed [event actor]
      (changed! event actor))))

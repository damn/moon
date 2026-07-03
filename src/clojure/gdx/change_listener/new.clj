(ns clojure.gdx.change-listener.new
  (:import (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn f [changed-fn]
  (proxy [ChangeListener] []
    (changed [event actor]
      (changed-fn event actor))))

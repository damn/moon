(ns clojure.scene2d.utils.click-listener
  (:import (com.badlogic.gdx.scenes.scene2d.utils ClickListener)))

(defn create [clicked-fn]
  (proxy [ClickListener] []
    (clicked [event x y]
      (clicked-fn event x y))))

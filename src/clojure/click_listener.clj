(ns clojure.click-listener
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.scenes.scene2d.utils ClickListener)))

(defn new [clicked-fn]
  (proxy [ClickListener] []
    (clicked [event x y]
      (clicked-fn event x y))))

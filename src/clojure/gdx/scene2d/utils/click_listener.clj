(ns clojure.gdx.scene2d.utils.click-listener
  (:import (com.badlogic.gdx.scenes.scene2d.utils ClickListener)))

(defn create [clicked]
  (proxy [ClickListener] []
    (clicked [event x y]
      (clicked event x y))))

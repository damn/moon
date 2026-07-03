(ns clojure.gdx.click-listener.new
  (:import (com.badlogic.gdx.scenes.scene2d.utils ClickListener)))

(defn f [clicked-fn]
  (proxy [ClickListener] []
    (clicked [event x y]
      (clicked-fn event x y))))

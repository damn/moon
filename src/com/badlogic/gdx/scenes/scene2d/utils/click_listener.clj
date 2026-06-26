(ns com.badlogic.gdx.scenes.scene2d.utils.click-listener
  (:import (com.badlogic.gdx.scenes.scene2d.utils ClickListener)))

(defn proxy-click-listener [f]
  (proxy [ClickListener] []
    (clicked [event x y]
      (f event x y))))

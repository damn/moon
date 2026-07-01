(ns clojure.gdx.viewport.unproject
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn f [viewport vector2]
  (Viewport/.unproject viewport ^Vector2 vector2))

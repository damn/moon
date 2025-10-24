(ns clojure.gdx.utils.screen
  (:import (com.badlogic.gdx.utils ScreenUtils)))

(defn clear!
  "Clears the color buffers with the specified color."
  [[r g b a]]
  (ScreenUtils/clear r g b a))

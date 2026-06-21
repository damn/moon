(ns clojure.bitmap-font.get-line-height
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn f [^BitmapFont font]
  (.getLineHeight font))

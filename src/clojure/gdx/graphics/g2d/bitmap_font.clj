(ns clojure.gdx.graphics.g2d.bitmap-font
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn data [^BitmapFont font]
  (.getData font))

(defn use-integer-positions! [^BitmapFont font bool]
  (.setUseIntegerPositions font bool))

(defn line-height [^BitmapFont font]
  (.getLineHeight font))

(defn draw! [^BitmapFont font batch text x y target-width h-align wrap?]
  (.draw font
         batch
         text
         (float x)
         (float y)
         (float target-width)
         h-align
         wrap?))

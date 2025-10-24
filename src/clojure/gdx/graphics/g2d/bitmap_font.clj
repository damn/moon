(ns clojure.gdx.graphics.g2d.bitmap-font
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn line-height [^BitmapFont font]
  (.getLineHeight font))

(defn data [^BitmapFont font]
  (.getData font))

(defn draw! [^BitmapFont font batch text x y target-width align wrap?]
  (.draw font
         batch
         text
         (float x)
         (float y)
         (float target-width)
         align
         wrap?))

(defn set-use-integer-positions! [^BitmapFont font use-integer-positions?]
  (.setUseIntegerPositions font use-integer-positions?))

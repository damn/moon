(ns com.badlogic.gdx.graphics.g2d.bitmap-font
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn data [^BitmapFont font]
  (.getData font))

(defn set-use-integer-positions! [^BitmapFont font use-integer-positions?]
  (.setUseIntegerPositions font use-integer-positions?))

(defn draw! [^BitmapFont font batch text x y target-width align wrap?]
  (.draw font
         batch
         text
         (float x)
         (float y)
         (float target-width)
         align
         wrap?))

(defn line-height [^BitmapFont font]
  (.getLineHeight font))

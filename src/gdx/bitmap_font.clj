(ns gdx.bitmap-font
  (:import (com.badlogic.gdx.graphics.g2d Batch
                                          BitmapFont)))

(defn get-data [font]
  (.getData ^BitmapFont font))

(defn draw! [font batch text x y target-width align wrap?]
  (.draw ^BitmapFont font
         ^Batch batch
         text
         (float x)
         (float y)
         (float target-width)
         align
         wrap?))

(defn get-line-height [font]
  (.getLineHeight ^BitmapFont font))

(defn set-use-integer-positions! [font use-integer-positions?]
  (.setUseIntegerPositions ^BitmapFont font use-integer-positions?))

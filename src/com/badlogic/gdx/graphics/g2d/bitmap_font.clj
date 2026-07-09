(ns com.badlogic.gdx.graphics.g2d.bitmap-font
  (:import (com.badlogic.gdx.graphics.g2d Batch
                                          BitmapFont)))

(defn draw [font ^Batch batch text x y target-width halign wrap?]
  (.draw ^BitmapFont font
         batch
         text
         (float x)
         (float y)
         (float target-width)
         halign
         wrap?))

(defn getData [font]
  (.getData ^BitmapFont font))

(defn getLineHeight [font]
  (.getLineHeight ^BitmapFont font))

(defn setUseIntegerPositions [font use-integer-positions?]
  (.setUseIntegerPositions ^BitmapFont font use-integer-positions?))

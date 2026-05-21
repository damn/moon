(ns clojure.gdx.graphics.g2d.bitmap-font
  (:require [clojure.gdx.utils.align :as align]
            [clojure.graphics.g2d.bitmap-font :as bitmap-font])
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(extend-type BitmapFont
  bitmap-font/BitmapFont
  (data [font]
    (.getData font))

  (line-height [font]
    (.getLineHeight font))

  (draw! [font batch text x y target-width align wrap?]
    (.draw font
           batch
           text
           (float x)
           (float y)
           (float target-width)
           (align/k->value align)
           wrap?))

  (set-use-integer-positions! [font use-integer-positions?]
    (.setUseIntegerPositions font use-integer-positions?)))

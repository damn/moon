(ns gdx.bitmap-font
  (:require [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]))

(defn get-data [font]
  (bitmap-font/getData font))

(defn draw! [font batch text x y target-width align wrap?]
  (bitmap-font/draw font batch text x y target-width align wrap?))

(defn get-line-height [font]
  (bitmap-font/getLineHeight font))

(defn set-use-integer-positions! [font use-integer-positions?]
  (bitmap-font/setUseIntegerPositions font use-integer-positions?))

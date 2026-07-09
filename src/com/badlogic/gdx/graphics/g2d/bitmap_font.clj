(ns com.badlogic.gdx.graphics.g2d.bitmap-font
  (:import (com.badlogic.gdx.graphics.g2d Batch BitmapFont)))

(defn draw [font ^Batch batch text x y target-width halign wrap?]
  (BitmapFont/.draw font batch text (float x) (float y) (float target-width) halign wrap?))

(defn get-data [font]
  (BitmapFont/.getData font))

(defn get-line-height [font]
  (BitmapFont/.getLineHeight font))

(defn set-use-integer-positions [font use-integer-positions?]
  (BitmapFont/.setUseIntegerPositions font use-integer-positions?))

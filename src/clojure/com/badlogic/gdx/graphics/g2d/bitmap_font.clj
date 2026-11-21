(ns clojure.com.badlogic.gdx.graphics.g2d.bitmap-font
  (:import (com.badlogic.gdx.graphics.g2d Batch BitmapFont)))

(defn data [^BitmapFont font]
  (.getData font))

(defn draw [^BitmapFont font ^Batch batch text x y target-width align wrap?]
  (.draw font batch text (float x) (float y) (float target-width) align wrap?))

(defn line-height [^BitmapFont font]
  (.getLineHeight font))

(defn set-use-integer-positions! [^BitmapFont font v]
  (.setUseIntegerPositions font v))

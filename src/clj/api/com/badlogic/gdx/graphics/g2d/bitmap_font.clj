(ns clj.api.com.badlogic.gdx.graphics.g2d.bitmap-font
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn data [^BitmapFont font]
  (.getData font))

(defn use-integer-positions! [^BitmapFont font bool]
  (.setUseIntegerPositions font bool))

(ns com.badlogic.gdx.graphics.g2d.bitmap-font
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn data [^BitmapFont font]
  (.getData font))

(defn set-use-integer-positions! [^BitmapFont font use-integer-positions?]
  (.setUseIntegerPositions font use-integer-positions?))

(ns bitmap-font.set-use-integer-positions
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn f! [^BitmapFont font use-integer-positions?]
  (.setUseIntegerPositions font use-integer-positions?))

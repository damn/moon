(ns clojure.gdx.bitmap-font.set-use-integer-positions
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn f [^BitmapFont font use-integer-positions?]
  (BitmapFont/.setUseIntegerPositions font use-integer-positions?))

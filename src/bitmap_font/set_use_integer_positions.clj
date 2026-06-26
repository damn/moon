(ns bitmap-font.set-use-integer-positions
  (:require [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]))

(defn f! [font use-integer-positions?]
  (bitmap-font/set-use-integer-positions! font use-integer-positions?))

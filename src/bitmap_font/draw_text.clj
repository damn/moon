(ns bitmap-font.draw-text
  (:require [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]))

(defn f! [font batch text x y target-width align wrap?]
  (bitmap-font/draw! font
                     batch
                     text
                     x
                     y
                     target-width
                     align
                     wrap?))

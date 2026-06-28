(ns bitmap-font.draw-text
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn f! [^BitmapFont font batch text x y target-width align wrap?]
  (.draw font
         batch
         text
         (float x)
         (float y)
         (float target-width)
         align
         wrap?))

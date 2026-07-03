(ns clojure.gdx.bitmap-font.draw!
  (:import (com.badlogic.gdx.graphics.g2d Batch BitmapFont)))

(defn f [^BitmapFont font ^Batch batch text x y target-width halign wrap?]
  (BitmapFont/.draw font batch text (float x) (float y) (float target-width) halign wrap?))

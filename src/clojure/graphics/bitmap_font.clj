(ns clojure.graphics.bitmap-font)

(defprotocol BitmapFont
  (scale-x [_])
  (set-scale! [_ scale])
  (enable-markup! [_ enable?])
  (use-integer-positions! [_ use-integer-positions?])
  (draw! [_ batch text x y target-width h-align wrap?])
  (text-height [_ text]))

(ns clojure.graphics.g2d.bitmap-font)

(defprotocol BitmapFont
  (data [_])
  (line-height [_])
  (draw! [_ batch text x y target-width align wrap?])
  (set-use-integer-positions! [_ use-integer-positions?]))

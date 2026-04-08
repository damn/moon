(ns gdl.graphics.batch)

(defprotocol Batch
  (draw! [_ texture-region x y origin-x origin-y width height scale-x scale-y rotation]
         [_ texture-region x y w h])
  (set-color! [_ r g b a])
  (set-projection-matrix! [_ matrix])
  (begin! [_])
  (end! [_]))

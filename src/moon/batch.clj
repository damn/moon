(ns moon.batch)

(defprotocol Batch
  (begin! [_])
  (end! [_])
  (set-color! [_ r g b a])
  (set-projection-matrix! [_ matrix])
  (draw! [_ texture-region x y origin-x origin-y width height scale-x scale-y rotation]
         [_ texture-region x y w h]))

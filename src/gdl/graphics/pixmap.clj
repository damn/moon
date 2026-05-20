(ns gdl.graphics.pixmap)

(defprotocol Pixmap
  (dispose! [_])
  (set-color! [_ r g b a])
  (draw-pixel! [_ x y])
  (texture [_]))

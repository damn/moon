(ns gdl.graphics.pixmap)

(defprotocol Pixmap
  (texture [_])
  (set-color! [_ r g b a])
  (draw-pixel! [_ x y])
  (dispose! [_]))

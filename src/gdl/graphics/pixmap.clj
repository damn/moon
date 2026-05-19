(ns gdl.graphics.pixmap)

(defprotocol Pixmap
  (set-color! [_ r g b a])
  (draw-pixel! [_ x y])
  (dispose! [_]))

(ns gdl.graphics.g2d.bitmap-font.data)

(defprotocol Data
  (scale-x [_])
  (set-scale! [_ scale])
  (set-markup-enabled! [_ enabled?]))

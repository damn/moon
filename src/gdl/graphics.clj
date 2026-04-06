(ns gdl.graphics)

(defprotocol Graphics
  (new-cursor [_ pixmap hotspot-x hotspot-y])
  (frames-per-second [_])
  (delta-time [_])
  (set-cursor! [_ cursor]))

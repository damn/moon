(ns clojure.graphics)

(defprotocol Graphics
  (frames-per-second [_])
  (delta-time [_])
  (new-cursor [_ pixmap hotspot-x hotspot-y])
  (set-cursor! [_ cursor])
  (gl20 [_]))

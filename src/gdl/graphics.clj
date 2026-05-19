(ns gdl.graphics)

(defprotocol Graphics
  (frames-per-second [_])
  (delta-time [_])
  (set-cursor! [_ cursor])
  (gl20 [_]))

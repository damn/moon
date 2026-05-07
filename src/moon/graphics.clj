(ns moon.graphics)

(defprotocol Graphics
  (frames-per-second [_])
  (delta-time [_])
  (set-cursor! [_ cursor])
  (clear! [_ r g b a]))

(ns clojure.graphics)

(defprotocol Graphics
  (new-cursor [_ file-handle hotspot-x hotspot-y])
  (frames-per-second [_])
  (delta-time [_])
  (set-cursor! [_ cursor])
  (clear! [_ r g b a])
  (white-pixel-texture [_]))

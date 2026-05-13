(ns moon.app)

(defprotocol App
  (new-sound [_ path])
  (frames-per-second [_])
  (delta-time [_])
  (new-cursor [_ pixmap hotspot-x hotspot-y])
  (set-cursor! [_ cursor])
  (clear! [_ r g b a])
  (set-input-processor! [_ input-processor])
  (key-pressed? [_ key])
  (key-just-pressed? [_ key])
  (button-just-pressed? [_ button])
  (mouse-position [_]))

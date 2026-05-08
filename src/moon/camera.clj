(ns moon.camera)

(defprotocol Camera
  (zoom [_])
  (visible-tiles [_])
  (frustum [_])
  (inc-zoom! [_ amount])
  (set-position! [_ position])
  (position [_])
  (combined [_]))

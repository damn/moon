(ns gdl.camera)

(defprotocol Camera
  (combined [_])
  (set-position! [camera [x y]])
  (set-zoom! [_ amount])
  (zoom [_])
  (frustum [_])
  (position [_])
  (viewport-width [_])
  (viewport-height [_]))


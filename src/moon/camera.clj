(ns moon.camera)

(defprotocol OrthographicCamera
  (combined [_])
  (frustum [_])
  (position [_])
  (viewport-width [_])
  (viewport-height [_])
  (zoom [_])
  (set-position! [_ [x y]])
  (set-zoom! [_ amount])
  (inc-zoom! [cam by])
  (calculate-zoom [_ & {:keys [left top right bottom]}])
  (visible-tiles [_]))






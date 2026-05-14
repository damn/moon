(ns moon.world-viewport)

(defprotocol Viewport
  (camera [_])
  (world-width [_])
  (world-height [_])
  (update! [_ screen-width screen-height center-camera?])
  (unproject [_ position]))

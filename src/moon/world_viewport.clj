(ns moon.world-viewport)

(defprotocol Viewport
  (world-width [_])
  (world-height [_])
  (update! [_ screen-width screen-height center-camera?])
  (unproject [_ position]))

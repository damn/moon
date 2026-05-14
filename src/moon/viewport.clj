(ns moon.viewport)

(defprotocol Viewport
  (update! [_ screen-width screen-height center-camera?])
  (unproject [_ position]))

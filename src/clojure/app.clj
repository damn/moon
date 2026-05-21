(ns clojure.app)

(defprotocol App
  (audio [_])
  (files [_])
  (graphics [_])
  (input [_]))

(declare sprite-batch
         clear-screen!
         fit-viewport
         tooltip-manager-set-initial-time!
         put-colors!
         pixmap
         orthographic-camera
         stage)

(ns clojure.app)

(defprotocol App
  (audio [_])
  (files [_])
  (graphics [_])
  (input [_]))

(declare tooltip-manager-set-initial-time!
         put-colors!
         pixmap
         orthographic-camera
         stage)

(ns clojure.app)

(defprotocol App
  (audio [_])
  (files [_])
  (graphics [_])
  (input [_]))

(declare put-colors!
         pixmap
         orthographic-camera
         stage)

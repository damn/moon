(ns gdl.app)

(defprotocol App
  (files [_])
  (graphics [_])
  (input [_]))

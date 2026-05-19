(ns gdl.app)

(defprotocol App
  (graphics [_])
  (input [_]))

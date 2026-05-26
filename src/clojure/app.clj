(ns clojure.app)

(defprotocol App
  (audio [_])
  (files [_])
  (graphics [_])
  (input [_]))

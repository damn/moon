(ns gdl.app)

(defprotocol App
  (audio [_])
  (files [_])
  (graphics [_])
  (input [_]))

(defprotocol SpriteBatch
  (sprite-batch [_]))

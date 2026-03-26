(ns moon.input)

(defprotocol Input
  (key-pressed? [_ key])
  (key-just-pressed? [_ key])
  (button-just-pressed? [_ button])
  (mouse-position [_])
  (player-movement-vector [_]))

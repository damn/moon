(ns gdl.input)

(defprotocol Input
  (key-pressed? [_ key])
  (key-just-pressed? [_ key])
  (button-just-pressed? [_ button])
  (x [_])
  (y [_])
  (set-processor! [_ input-processor]))

(ns clojure.input)

(defprotocol Input
  (set-processor! [_ input-processor])
  (key-pressed? [_ key])
  (key-just-pressed? [_ key])
  (button-just-pressed? [_ button])
  (mouse-position [_]))

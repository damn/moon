(ns clojure.gdx.app)

(defprotocol App
  (audio [_])
  (files [_])
  (graphics [_])
  (set-input-processor! [_ input-processor])
  (key-pressed? [_ key])
  (key-just-pressed? [_ key])
  (button-just-pressed? [_ button])
  (mouse-position [_]))

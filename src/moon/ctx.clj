(ns moon.ctx)

(defprotocol Context
  (world-unit-scale [_])
  (mouse-position [_])
  (button-just-pressed? [_ button])
  (key-just-pressed? [_ key])
  (item-place-position [_ player-entity])
  (sound-names [_])
  (draw! [_ draws])
  (do! [_ txs]))

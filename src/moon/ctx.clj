(ns moon.ctx)

(defprotocol Context
  (world-unit-scale [_])
  (mouse-position [_])
  (button-just-pressed? [_ button])
  (item-place-position [_ player-entity]))

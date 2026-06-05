(ns handle-input.player-idle
  (:require [game.ctx.button-just-pressed :refer [button-just-pressed?]]
            [clojure.input.buttons :as input.buttons]
            [game.ctx.player-movement-vector :refer [player-movement-vector]]
            [handle-input.player-idle.interaction-state-txs :refer [interaction-state->txs]]))

(defn f
  [player-eid {:keys [ctx/interaction-state
                      ctx/stage]
               :as ctx}]
  (if-let [movement-vector (player-movement-vector ctx)]
    [[:tx/event player-eid :movement-input movement-vector]]
    (when (button-just-pressed? ctx input.buttons/left)
      (interaction-state->txs interaction-state
                              stage
                              player-eid))))

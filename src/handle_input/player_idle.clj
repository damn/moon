(ns handle-input.player-idle
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.input :as input]
            [clojure.gdx.input.buttons :as input.buttons]
            [game.ctx.player-movement-vector :refer [player-movement-vector]]
            [handle-input.player-idle.interaction-state-txs :refer [interaction-state->txs]]))

(defn f
  [player-eid {:keys [ctx/app
                      ctx/interaction-state
                      ctx/stage]
               :as ctx}]
  (if-let [movement-vector (player-movement-vector ctx)]
    [[:tx/event player-eid :movement-input movement-vector]]
    (when (input/button-just-pressed? (app/input app) input.buttons/left)
      (interaction-state->txs interaction-state
                              stage
                              player-eid))))

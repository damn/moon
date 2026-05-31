(ns render.handle-player-input
  (:require [game.ctx.do :refer [do!]]
            [game.state :as state]))

(defn step
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (state/handle-input [state-k (state-k entity)] eid ctx)]
    (do! ctx txs))
  ctx)

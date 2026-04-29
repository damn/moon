(ns moon.application.render.handle-input
  (:require [moon.state :as state]
            [moon.txs :as txs]))

(defn step
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (state/handle-input [state-k (state-k entity)] eid ctx)]
    (txs/handle! ctx txs))
  ctx)

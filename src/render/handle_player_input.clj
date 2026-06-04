(ns render.handle-player-input
  (:require [game.ctx.do :refer [do!]]))

(defn step
  [{:keys [ctx/k->handle-input
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (if-let [f (k->handle-input state-k)]
              (f eid ctx)
              nil)]
    (do! ctx txs))
  ctx)

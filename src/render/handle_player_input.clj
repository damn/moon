(ns render.handle-player-input
  (:require [game.ctx.do :refer [do!]]
            handle-input.player-idle
            handle-input.player-moving
            handle-input.player-item-on-cursor))

(def k->fn
  {
   :player-idle handle-input.player-idle/f
   :player-moving handle-input.player-moving/f
   :player-item-on-cursor handle-input.player-item-on-cursor/f
   }
  )

(defn step
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (if-let [f (k->fn state-k)]
              (f eid ctx)
              nil)]
    (do! ctx txs))
  ctx)

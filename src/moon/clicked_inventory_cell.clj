(ns moon.clicked-inventory-cell
  (:require [moon.ctx :as ctx]
            [moon.entity.state :as state]))

(defn do! [cell {:keys [ctx/world] :as ctx}]
  (let [eid (:world/player-eid world)
        entity @eid
        state-k (:state (:entity/fsm entity))]
    (ctx/handle! ctx
                 (state/clicked-inventory-cell [state-k (state-k entity)]
                                               eid
                                               cell))))

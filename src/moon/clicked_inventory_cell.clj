(ns moon.clicked-inventory-cell
  (:require [moon.ctx :as ctx]
            [moon.entity.state :as state]))

(defn do! [cell {:keys [ctx/player-eid] :as ctx}]
  (let [entity @player-eid
        state-k (:state (:entity/fsm entity))]
    (ctx/handle! ctx
                 (state/clicked-inventory-cell [state-k (state-k entity)]
                                               player-eid
                                               cell))))

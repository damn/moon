(ns moon.application.create.add-stage-actors.windows.inventory
  (:require [moon.state :as state]
            [moon.txs :as txs]
            [moon.ui-actors.windows.inventory]))

(defn create [ctx]
  (moon.ui-actors.windows.inventory/create ctx
                                           (fn clicked-inventory-cell [cell {:keys [ctx/player-eid] :as ctx}]
                                             (let [entity @player-eid
                                                   state-k (:state (:entity/fsm entity))]
                                               (txs/handle! ctx
                                                            (state/clicked-inventory-cell [state-k (state-k entity)]
                                                                                          player-eid
                                                                                          cell))))))

(ns moon.application.create.spawn-player
  (:require [moon.db :as db]
            [moon.txs :as txs]))

(defn step
  [{:keys [ctx/db
           ctx/entity-ids
           ctx/start-position]
    :as ctx}]
  (txs/handle! ctx
               [[:tx/spawn-creature {:position (mapv (partial + 0.5) start-position)
                                     :creature-property (db/build db :creatures/vampire)
                                     :components {:entity/fsm {:fsm :fsms/player
                                                               :initial-state :player-idle}
                                                  :entity/faction :good
                                                  :entity/player? true
                                                  :entity/free-skill-points 3
                                                  :entity/clickable {:type :clickable/player}
                                                  :entity/click-distance-tiles 1.5}}]])
  (let [eid (get @entity-ids 1)]
    (assert (:entity/player? @eid))
    (assoc ctx :ctx/player-eid eid)))

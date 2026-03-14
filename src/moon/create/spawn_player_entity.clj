(ns moon.create.spawn-player-entity
  (:require [moon.txs :as txs]
            [moon.db :as db]))

(defn step
  [{:keys [ctx/db
           ctx/entity-ids
           ctx/start-position]
    :as ctx}
   {:keys [creature-id
           components]}]
  (txs/handle! ctx
               [[:tx/spawn-creature {:position (mapv (partial + 0.5) start-position)
                                     :creature-property (db/build db creature-id)
                                     :components components}]])
  (let [eid (get @entity-ids 1)]
    (assert (:entity/player? @eid))
    (assoc ctx :ctx/player-eid eid)))

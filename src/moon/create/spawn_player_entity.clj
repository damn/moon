(ns moon.create.spawn-player-entity
  (:require [moon.db :as db]
            [moon.ctx :as ctx]))

(defn step
  [{:keys [ctx/db
           ctx/world]
    :as ctx}]
  (ctx/handle! ctx
               [[:tx/spawn-creature (let [{:keys [creature-id
                                                  components]} (:world/player-components world)]
                                      {:position (mapv (partial + 0.5) (:world/start-position world))
                                       :creature-property (db/build db creature-id)
                                       :components components})]])
  (let [eid (get @(:world/entity-ids world) 1)]
    (assert (:entity/player? @eid))
    (assoc ctx :ctx/player-eid eid)))

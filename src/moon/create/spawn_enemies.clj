(ns moon.create.spawn-enemies
  (:require [clojure.gdx.maps.tiled.tiled-map :as tiled-map]
            [moon.db :as db]
            [moon.txs :as txs]))

(defn step
  [{:keys [ctx/db
           ctx/tiled-map]
    :as ctx}]
  (txs/handle!
   ctx
   (for [[position creature-id] (tiled-map/spawn-positions tiled-map)]
     [:tx/spawn-creature {:position (mapv (partial + 0.5) position)
                          :creature-property (db/build db (keyword creature-id))
                          :components {:entity/fsm {:fsm :fsms/npc
                                                    :initial-state :npc-sleeping}
                                       :entity/faction :evil}}]))
  ctx)

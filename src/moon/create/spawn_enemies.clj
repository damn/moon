(ns moon.create.spawn-enemies
  (:require [moon.ctx :as ctx]
            [moon.db :as db]
            [moon.world.tiled-map :as tiled-map]))

(defn step
  [{:keys [ctx/db
           ctx/world]
    :as ctx}]
  (ctx/handle!
   ctx
   (for [[position creature-id] (tiled-map/spawn-positions (:world/tiled-map world))]
     [:tx/spawn-creature {:position (mapv (partial + 0.5) position)
                          :creature-property (db/build db (keyword creature-id))
                          :components (:world/enemy-components world)}]))
  ctx)

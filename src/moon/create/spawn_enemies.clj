(ns moon.create.spawn-enemies
  (:require [moon.ctx :as ctx]
            [moon.db :as db]
            [moon.tiled-map :as tiled-map]))

(defn step
  [{:keys [ctx/db
           ctx/tiled-map]
    :as ctx}
   components]
  (ctx/handle!
   ctx
   (for [[position creature-id] (tiled-map/spawn-positions tiled-map)]
     [:tx/spawn-creature {:position (mapv (partial + 0.5) position)
                          :creature-property (db/build db (keyword creature-id))
                          :components components}]))
  ctx)

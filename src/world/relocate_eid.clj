(ns world.relocate-eid
  (:require [moon.content-grid :as content-grid]
            [moon.grid :as grid]))

(defn relocate-eid!
  [{:keys [world/content-grid
           world/grid]}
   eid]
  (content-grid/update-entity! content-grid eid)
  (grid/remove-from-touched-cells! grid eid)
  (grid/set-touched-cells! grid eid)
  (when (:entity/collides? @eid)
    (grid/remove-from-occupied-cells! grid eid)
    (grid/set-occupied-cells! grid eid))
  nil)

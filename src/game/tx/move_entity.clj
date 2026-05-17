(ns game.tx.move-entity
  (:require [moon.content-grid :as content-grid]
            [moon.grid :as grid]))

(defn do!
  [{:keys [ctx/content-grid
           ctx/grid]}
   eid]
  (content-grid/position-changed! content-grid eid)
  (grid/remove-from-touched-cells! grid eid)
  (grid/set-touched-cells! grid eid)
  (when (:body/collides? (:entity/body @eid))
    (grid/remove-from-occupied-cells! grid eid)
    (grid/set-occupied-cells! grid eid))
  nil)

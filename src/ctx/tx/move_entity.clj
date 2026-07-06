(ns ctx.tx.move-entity
  (:require [moon.content-grid.update-entity :as update-entity]
            [moon.grid.remove-from-occupied-cells :refer [remove-from-occupied-cells!]]
            [moon.grid.set-occupied-cells :refer [set-occupied-cells!]]
            [moon.grid.remove-from-touched-cells :refer [remove-from-touched-cells!]]
            [moon.grid.set-touched-cells :refer [set-touched-cells!]]))

(defn do!
  [{:keys [ctx/content-grid
           ctx/grid]}
   eid]
  (update-entity/f! content-grid eid)
  (remove-from-touched-cells! grid eid)
  (set-touched-cells! grid eid)
  (when (:body/collides? (:entity/body @eid))
    (remove-from-occupied-cells! grid eid)
    (set-occupied-cells! grid eid))
  nil)

(ns moon.grid.npc-pathing
  (:require [clojure.direction :as direction]
            [moon.cell.is-occupied-by-other :as occupied-by-other?]
            [moon.grid.npc-pathing.find-next-cell :as find-next-cell]
            [moon.grid.npc-pathing.inside-cell :as inside-cell?]))

(defn find-direction [grid eid]
  (let [position (:body/position (:entity/body @eid))
        own-cell (grid (mapv int position))
        {:keys [target-entity target-cell]} (find-next-cell/f grid eid own-cell)]
    (cond
     target-entity
     (direction/f position (:body/position (:entity/body @target-entity)))

     (nil? target-cell)
     nil

     :else
     (when-not (and (= target-cell own-cell)
                    (occupied-by-other?/f @own-cell eid)) ; prevent friction 2 move to center
       (when-not (inside-cell?/f grid @eid target-cell)
         (direction/f position (:middle @target-cell)))))))

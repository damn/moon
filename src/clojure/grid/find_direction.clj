(ns clojure.grid.find-direction
  (:require [clojure.grid.find-next-cell :refer [find-next-cell]]
            [clojure.grid.inside-cell :refer [inside-cell?]]
            [clojure.is-occupied-by-other :as occupied-by-other?]
            [moon.v2 :as v2]))

(defn find-direction [grid eid]
  (let [position (:body/position (:entity/body @eid))
        own-cell (grid (mapv int position))
        {:keys [target-entity target-cell]} (find-next-cell grid eid own-cell)]
    (cond
      target-entity
      (v2/direction position (:body/position (:entity/body @target-entity)))

      (nil? target-cell)
      nil

      :else
      (when-not (and (= target-cell own-cell)
                     (occupied-by-other?/f @own-cell eid)) ; prevent friction 2 move to center
        (when-not (inside-cell? grid @eid target-cell)
          (v2/direction position (:middle @target-cell)))))))

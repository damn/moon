(ns moon.grid.npc-pathing.get-min-dist-cell)

(defn get-min-dist-cell [distance-to cells]
  (let [cells (filter distance-to cells)]
    (when (seq cells)
      (apply min-key distance-to cells))))

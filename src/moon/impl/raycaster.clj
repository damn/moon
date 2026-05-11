(ns moon.impl.raycaster
  (:require [moon.grid2d :as g2d]
            [moon.cell :as cell]))

(defn create [{:keys [ctx/grid]}]
  (let [width  (g2d/width  grid)
        height (g2d/height grid)
        cells  (for [cell (map deref (g2d/cells grid))]
                 [(:position cell)
                  (boolean (cell/blocks-vision? cell))])]
    (let [arr (make-array Boolean/TYPE width height)]
      (doseq [[[x y] blocked?] cells]
        (aset arr x y (boolean blocked?)))
      [arr width height])))

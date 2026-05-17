(ns game.impl.raycaster
  (:require [clojure.math.raycaster :as raycaster]
            [moon.grid2d :as g2d]
            [moon.cell :as cell]
            [moon.raycaster]))

(defn create [{:keys [ctx/grid]}]
  (let [width  (g2d/width  grid)
        height (g2d/height grid)
        cells  (for [cell (map deref (g2d/cells grid))]
                 [(:position cell)
                  (boolean (cell/blocks-vision? cell))])]
    (let [arr (make-array Boolean/TYPE width height)]
      (doseq [[[x y] blocked?] cells]
        (aset arr x y (boolean blocked?)))

      (let [this [arr width height]]
        (reify moon.raycaster/Raycaster
          (blocked? [_ [start-x start-y] [target-x target-y]]
            (raycaster/blocked? this
                                [start-x start-y]
                                [target-x target-y]))

          (line-of-sight? [_ source target]
            (not (raycaster/blocked? this
                                     (:body/position (:entity/body source))
                                     (:body/position (:entity/body target))))))))))

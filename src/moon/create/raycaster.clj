(ns moon.create.raycaster
  (:require [clojure.math.raycaster :as raycaster]
            [moon.cell :as cell]
            [moon.grid2d :as g2d]
            [moon.raycaster]))

(defn step [{:keys [ctx/grid] :as ctx}]
  (assoc ctx :ctx/raycaster (let [width  (g2d/width  grid)
                                  height (g2d/height grid)
                                  cells  (for [cell (map deref (g2d/cells grid))]
                                           [(:position cell)
                                            (boolean (cell/blocks-vision? cell))])]
                              (let [arr (make-array Boolean/TYPE width height)]
                                (doseq [[[x y] blocked?] cells]
                                  (aset arr x y (boolean blocked?)))
                                (reify moon.raycaster/Raycaster
                                  (blocked? [_ [start-x start-y] [target-x target-y]]
                                    (raycaster/blocked? [arr width height] [start-x start-y] [target-x target-y])))))))

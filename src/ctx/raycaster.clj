(ns ctx.raycaster
  (:require [clojure.grid2d.cells :refer [->cells]]
            [clojure.grid2d.width :refer [->width]]
            [clojure.grid2d.height :refer [->height]]
            [moon.cell.blocks-vision :as blocks-vision?]))

(defn step
  [{:keys [ctx/grid]}]
  (let [width (->width grid)
        height (->height grid)
        cells (for [cell (map deref (->cells grid))]
                [(:position cell)
                 (boolean (blocks-vision?/f cell))])]
    (let [arr (make-array Boolean/TYPE width height)]
      (doseq [[[x y] blocked?] cells]
        (aset arr x y (boolean blocked?)))
      [arr width height])))

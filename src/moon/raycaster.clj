(ns moon.raycaster
  (:require [clojure.math.raycaster :as raycaster]
            [clojure.grid2d.cells :refer [->cells]]
            [clojure.grid2d :as g2d]
            [moon.cell :as cell]))

(defn create [grid]
  (let [width  (g2d/width  grid)
        height (g2d/height grid)
        cells  (for [cell (map deref (->cells grid))]
                 [(:position cell)
                  (boolean (cell/blocks-vision? cell))])]
    (let [arr (make-array Boolean/TYPE width height)]
      (doseq [[[x y] blocked?] cells]
        (aset arr x y (boolean blocked?)))
      [arr width height])))

(defn blocked? [this [start-x start-y] [target-x target-y]]
  (raycaster/blocked? this
                      [start-x start-y]
                      [target-x target-y]))

(defn line-of-sight? [this source target]
  (not (raycaster/blocked? this
                           (:body/position (:entity/body source))
                           (:body/position (:entity/body target)))))

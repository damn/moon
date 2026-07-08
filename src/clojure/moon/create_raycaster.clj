(ns clojure.moon.create-raycaster
  (:require [clojure.blocks-vision :as blocks-vision?]
            [clojure.cells :refer [->cells]]
            [clojure.height :refer [->height]]
            [clojure.width :refer [->width]]))

(defn f [ctx]
  (let [grid (:ctx/grid ctx)
        width (->width grid)
        height (->height grid)
        cells (for [cell (map deref (->cells grid))]
                [(:position cell)
                 (boolean (blocks-vision?/f cell))])
        arr (make-array Boolean/TYPE width height)]
    (doseq [[[x y] blocked?] cells]
      (aset arr x y (boolean blocked?)))
    (assoc ctx :ctx/raycaster [arr width height])))

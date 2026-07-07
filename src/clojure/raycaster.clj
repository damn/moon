(ns clojure.raycaster
  (:require [clojure.cells :refer [->cells]]
            [clojure.width :refer [->width]]
            [clojure.height :refer [->height]]
            [clojure.blocks-vision :as blocks-vision?]))

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

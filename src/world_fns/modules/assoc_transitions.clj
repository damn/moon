(ns world-fns.modules.assoc-transitions
  (:require [clojure.grid2d.cells :refer [->cells]]
            [clojure.grid2d.adjacent-wall-positions :as adjacent-wall-positions]))

(defn f
  [{:keys [grid] :as world-fn-ctx}]
  (let [grid (reduce #(assoc %1 %2 :transition)
                     grid
                     (adjacent-wall-positions/f grid))]
    (assert (or
             (= #{:wall :ground :transition} (set (->cells grid)))
             (= #{:ground :transition} (set (->cells grid))))
            (str "(set (->cells grid)): " (set (->cells grid))))
    (assoc world-fn-ctx :grid grid)))

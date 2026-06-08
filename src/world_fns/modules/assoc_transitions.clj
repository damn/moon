(ns world-fns.modules.assoc-transitions
  (:require [clojure.grid2d :as g2d]
            [clojure.grid2d.adjacent-wall-positions :as adjacent-wall-positions]))

(defn f
  [{:keys [grid] :as world-fn-ctx}]
  (let [grid (reduce #(assoc %1 %2 :transition)
                     grid
                     (adjacent-wall-positions/f grid))]
    (assert (or
             (= #{:wall :ground :transition} (set (g2d/cells grid)))
             (= #{:ground :transition} (set (g2d/cells grid))))
            (str "(set (g2d/cells grid)): " (set (g2d/cells grid))))
    (assoc world-fn-ctx :grid grid)))

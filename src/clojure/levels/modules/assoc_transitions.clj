(ns clojure.levels.modules.assoc-transitions
  (:require [clojure.g2d.cells :refer [->cells]]
            [moon.g2d :as g2d]))

(defn f
  [{:keys [grid] :as world-fn-ctx}]
  (let [grid (reduce #(assoc %1 %2 :transition)
                     grid
                     (g2d/adjacent-wall-positions grid))]
    (assert (or
             (= #{:wall :ground :transition} (set (->cells grid)))
             (= #{:ground :transition} (set (->cells grid))))
            (str "(set (->cells grid)): " (set (->cells grid))))
    (assoc world-fn-ctx :grid grid)))

(ns world-fns.modules.print-grid
  (:require [grid2d.printgrid :as printgrid]))

(defn print-grid [{:keys [grid] :as world-fn-ctx}]
  (printgrid/f grid)
  (println " - ")
  world-fn-ctx)

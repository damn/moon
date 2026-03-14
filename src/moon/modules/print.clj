(ns moon.modules.print
  (:require [moon.grid2d :as g2d]))

(defn step [{:keys [grid] :as world-fn-ctx}]
  (g2d/printgrid grid)
  (println " - ")
  world-fn-ctx)

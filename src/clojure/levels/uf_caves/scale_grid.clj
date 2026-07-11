(ns clojure.levels.uf-caves.scale-grid
  (:require [moon.g2d :as g2d]))

(defn f [grid start scale]
  (let [grid (g2d/scale-uniform grid scale)]
    ;_ (printgrid/f grid)
    ;_ (println)
    {:start-position (mapv #(* % scale) start)
     :grid grid}))

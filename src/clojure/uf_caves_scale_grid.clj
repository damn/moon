(ns clojure.uf-caves-scale-grid
  (:require [clojure.scalegrid :as scalegrid]))

(defn f [grid start scale]
  (let [grid (scalegrid/f grid scale)]
    ;_ (printgrid/f grid)
    ;_ (println)
    {:start-position (mapv #(* % scale) start)
     :grid grid}))

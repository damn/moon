(ns clojure.print-grid
  (:require [clojure.printgrid :as printgrid]))

(defn print-grid [{:keys [grid] :as world-fn-ctx}]
  (printgrid/f grid)
  (println " - ")
  world-fn-ctx)

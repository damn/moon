(ns clojure.print-grid
  (:require [moon.g2d :as g2d]))

(defn print-grid [{:keys [grid] :as world-fn-ctx}]
  (g2d/print-y-up grid)
  (println " - ")
  world-fn-ctx)

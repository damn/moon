(ns moon.modules.print
  (:require [moon.world-fns.utils :as helper]))

(defn step [{:keys [grid] :as world-fn-ctx}]
  (helper/printgrid grid)
  (println " - ")
  world-fn-ctx)

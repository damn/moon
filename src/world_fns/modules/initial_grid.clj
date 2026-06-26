(ns world-fns.modules.initial-grid
  (:require [clojure.java.util.random :as random]))

(defn f
  [{:keys [initial-grid-fn
           grid2d-fix-nads-fn
           world/map-size]
    :as world-fn-ctx}]
  (let [{:keys [start grid]} (initial-grid-fn (random/create) map-size map-size :wide)
        grid (grid2d-fix-nads-fn grid)]
    (assoc world-fn-ctx
           :start start
           :grid grid)))

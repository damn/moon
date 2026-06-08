(ns world-fns.modules
  (:require [world-fns.modules.load-schema-tiled-map :as load-schema-tiled-map]
            [world-fns.modules.print-grid :refer [print-grid]]
            [world-fns.modules.calculate-start :as calculate-start]
            [world-fns.modules.assoc-transitions :as assoc-transitions]
            [world-fns.modules.create-scaled-grid :as create-scaled-grid]
            [world-fns.modules.last-steps]
            [world-fns.modules.place :as place-module]
            [world-fns.modules.convert-to-tiled-map :as convert-to-tiled-map]
            [world-fns.modules.initial-grid :as initial-grid]))

(defn create
  [{:keys [world/map-size
           world/max-area-level
           steps]
    :as world-fn-ctx}]
  (assert (<= max-area-level map-size))
  (-> world-fn-ctx
      (assoc :scale [32 20])
      initial-grid/f
      #_print-grid
      assoc-transitions/f
      #_print-grid
      create-scaled-grid/f
      load-schema-tiled-map/f
      place-module/step
      convert-to-tiled-map/f
      calculate-start/f
      world-fns.modules.last-steps/step))

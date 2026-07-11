(ns clojure.levels.modules
  (:require [moon.caves :as caves]
            [moon.g2d :as g2d]
            [clojure.load-schema-tiled-map :as load-schema-tiled-map]
            [clojure.print-grid :refer [print-grid]]
            [clojure.levels.modules.calculate-start :as calculate-start]
            [clojure.levels.modules.assoc-transitions :as assoc-transitions]
            [clojure.levels.modules.create-scaled-grid :as create-scaled-grid]
            [clojure.levels.modules.last-steps]
            [clojure.levels.modules.place :as place-module]
            [clojure.levels.modules.convert-to-tiled-map :as convert-to-tiled-map]
            [clojure.levels.modules.initial-grid :as initial-grid]))

(defn create
  [world-fn-ctx]
  (let [world-fn-ctx (merge {:initial-grid-fn caves/create
                             :grid2d-fix-nads-fn g2d/fix-nads
                             :world/map-size 5
                             :world/max-area-level 3
                             :world/spawn-rate 0.05}
                            world-fn-ctx)
        {:keys [world/map-size
                world/max-area-level
                steps]} world-fn-ctx]
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
      clojure.levels.modules.last-steps/step)))

(ns clojure.modules
  (:require [clojure.caves :as caves]
            [clojure.fix-nads :as fix-nads]
            [clojure.load-schema-tiled-map :as load-schema-tiled-map]
            [clojure.print-grid :refer [print-grid]]
            [clojure.modules.calculate-start :as calculate-start]
            [clojure.modules.assoc-transitions :as assoc-transitions]
            [clojure.modules.create-scaled-grid :as create-scaled-grid]
            [clojure.modules.last-steps]
            [clojure.modules.place :as place-module]
            [clojure.modules.convert-to-tiled-map :as convert-to-tiled-map]
            [clojure.modules.initial-grid :as initial-grid]))

(defn create
  [world-fn-ctx]
  (let [world-fn-ctx (merge {:initial-grid-fn caves/create
                             :grid2d-fix-nads-fn fix-nads/f
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
      clojure.modules.last-steps/step)))

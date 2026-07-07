(ns clojure.modules
  (:require [clojure.load-schema-tiled-map :as load-schema-tiled-map]
            [clojure.print-grid :refer [print-grid]]
            [clojure.calculate-start :as calculate-start]
            [clojure.assoc-transitions :as assoc-transitions]
            [clojure.create-scaled-grid :as create-scaled-grid]
            [clojure.modules-last-steps]
            [clojure.place :as place-module]
            [clojure.convert-to-tiled-map :as convert-to-tiled-map]
            [clojure.modules-initial-grid :as initial-grid]))

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
      clojure.modules-last-steps/step))

(ns world-fns.modules
  (:require [clojure.grid2d.adjacent-wall-positions :as adjacent-wall-positions]
            [com.badlogic.gdx.maps.tiled.tmx-map-loader :as tmx-map-loader]
            [world-fns.modules.print-grid :refer [print-grid]]
            [com.badlogic.gdx.maps.tiled.tiled-map.create :as create-tiled-map]
            [clojure.grid2d :as g2d]
            [clojure.grid2d.scale-grid :as scale-grid]
            [world-fns.modules.grid-to-tiled-map :refer [grid->tiled-map]]
            [world-fns.modules.last-steps]
            [world-fns.modules.place :as place-module])
  (:import (java.util Random)))

(defn- initial-grid
  [{:keys [initial-grid-fn
           grid2d-fix-nads-fn
           world/map-size]
    :as world-fn-ctx}]
  (let [{:keys [start grid]} (initial-grid-fn (Random.) map-size map-size :wide)
        grid (grid2d-fix-nads-fn grid)]
    (assoc world-fn-ctx
           :start start
           :grid grid)))

(defn- assoc-transitions
  [{:keys [grid] :as world-fn-ctx}]
  (let [grid (reduce #(assoc %1 %2 :transition)
                     grid
                     (adjacent-wall-positions/f grid))]
    (assert (or
             (= #{:wall :ground :transition} (set (g2d/cells grid)))
             (= #{:ground :transition} (set (g2d/cells grid))))
            (str "(set (g2d/cells grid)): " (set (g2d/cells grid))))
    (assoc world-fn-ctx :grid grid)))

(defn- create-scaled-grid [w]
  (assoc w :scaled-grid (scale-grid/f (:grid w) (:scale w))))

(defn- load-schema-tiled-map [w]
  (assoc w :schema-tiled-map (tmx-map-loader/load! "maps/modules.tmx")))

(defn- convert-to-tiled-map
  [{:keys [scaled-grid
           schema-tiled-map]
    :as w}]
  (assoc w :tiled-map (create-tiled-map/f (grid->tiled-map schema-tiled-map scaled-grid))))

(defn- calculate-start-position [{:keys [start scale] :as w}]
  (assoc w :start-position (mapv * start scale)))

(defn create
  [{:keys [world/map-size
           world/max-area-level
           steps]
    :as world-fn-ctx}]
  (assert (<= max-area-level map-size))
  (-> world-fn-ctx
      (assoc :scale [32 20])
      initial-grid
      #_print-grid
      assoc-transitions
      #_print-grid
      create-scaled-grid
      load-schema-tiled-map
      place-module/step
      convert-to-tiled-map
      calculate-start-position
      world-fns.modules.last-steps/step))

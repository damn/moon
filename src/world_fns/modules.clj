(ns world-fns.modules
  (:require [com.badlogic.gdx.maps.map-properties :as props]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]
            [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tmx-map-loader :as tmx-map-loader]
            [moon.grid2d :as g2d]
            [clojure.grid2d.printgrid :as printgrid]
            [clojure.grid2d.adjacent-wall-positions :as adjacent-wall-positions]
            [moon.tiled-map]
            [world-fns.modules.place :as place-module]
            world-fns.modules.last-steps)
  (:import (java.util Random)))

(defn- print-grid [{:keys [grid] :as world-fn-ctx}]
  (printgrid/f grid)
  (println " - ")
  world-fn-ctx)

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
  (assoc w :scaled-grid (g2d/scale-grid (:grid w) (:scale w))))

(defn- load-schema-tiled-map [w]
  (assoc w :schema-tiled-map (tmx-map-loader/load! "maps/modules.tmx")))

(def copy-tile
  (memoize
   (fn [tile]
     (assert tile)
     (static-tiled-map-tile/copy tile))))

(defn- grid->tiled-map
  [schema-tiled-map grid]
  {:properties (merge (props/->clj (tiled-map/props schema-tiled-map))
                      {"width" (g2d/width grid)
                       "height" (g2d/height grid)})
   :layers (for [layer (tiled-map/layers schema-tiled-map)]
             {:name (layer/name layer)
              :visible? (layer/visible? layer)
              :properties (props/->clj (layer/properties layer))
              :tiles (for [position (g2d/posis grid)
                           :let [local-position (get grid position)]
                           :when local-position]
                       (when (vector? local-position)
                         (when-let [cell (layer/cell layer local-position)]
                           [position (copy-tile (cell/tile cell))])))})})

(defn- convert-to-tiled-map
  [{:keys [scaled-grid
           schema-tiled-map]
    :as w}]
  (assoc w :tiled-map (moon.tiled-map/create-map (grid->tiled-map schema-tiled-map scaled-grid))))

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

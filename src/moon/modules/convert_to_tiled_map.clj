(ns moon.modules.convert-to-tiled-map
  (:require [clj.api.com.badlogic.gdx.maps.map-properties :as props]
            [clj.api.com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [clj.api.com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]
            [moon.grid2d :as g2d]
            [moon.tiled-map :as tiled-map])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn- props->clj [map-properties]
  (zipmap (props/keys   map-properties)
          (props/values map-properties)))

(defn- grid->tiled-map
  [^TiledMap schema-tiled-map grid]
  (tiled-map/create-tiled-map
   {:properties (merge (props->clj (.getProperties schema-tiled-map))
                       {"width" (g2d/width grid)
                        "height" (g2d/height grid)})
    :layers (for [layer (.getLayers schema-tiled-map)]
              {:name (layer/name layer)
               :visible? (layer/visible? layer)
               :properties (props->clj (layer/properties layer))
               :tiles (for [position (g2d/posis grid)
                            :let [local-position (get grid position)]
                            :when local-position]
                        (when (vector? local-position)
                          (when-let [cell (layer/cell layer local-position)]
                            [position (tiled-map/copy-tile (cell/tile cell))])))})}))

(defn step
  [{:keys [scaled-grid
           schema-tiled-map]
    :as w}]
  (assoc w :tiled-map (grid->tiled-map schema-tiled-map scaled-grid)))

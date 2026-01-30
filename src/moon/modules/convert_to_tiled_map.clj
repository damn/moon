(ns moon.modules.convert-to-tiled-map
  (:require [clojure.grid2d :as g2d]
            [moon.tiled-map :as tiled-map])
  (:import (com.badlogic.gdx.maps MapProperties)
           (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer)))

(defn- props->clj [^MapProperties map-properties]
  (zipmap (.getKeys   map-properties)
          (.getValues map-properties)))

(defn- grid->tiled-map
  [^TiledMap schema-tiled-map grid]
  (tiled-map/create-tiled-map
   {:properties (merge (props->clj (.getProperties schema-tiled-map))
                       {"width" (g2d/width grid)
                        "height" (g2d/height grid)})
    :layers (for [^TiledMapTileLayer layer (.getLayers schema-tiled-map)]
              {:name (.getName layer)
               :visible? (.isVisible layer)
               :properties (props->clj (.getProperties layer))
               :tiles (for [position (g2d/posis grid)
                            :let [local-position (get grid position)]
                            :when local-position]
                        (when (vector? local-position)
                          (when-let [cell (let [[x y] local-position]
                                            (.getCell layer x y))]
                            [position (tiled-map/copy-tile (.getTile cell))])))})}))

(defn step
  [{:keys [scaled-grid
           schema-tiled-map]
    :as w}]
  (assoc w :tiled-map (grid->tiled-map schema-tiled-map scaled-grid)))

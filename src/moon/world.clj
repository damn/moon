(ns moon.world
  (:require [moon.tiled-map :as tiled-map]
            [moon.cell :as cell]
            [moon.content-grid :as content-grid]
            [moon.g2d :as g2d])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn raycaster-data [world]
  (let [grid (:world/grid world)
        width (g2d/width grid)
        height (g2d/height grid)
        cells (for [cell (map deref (g2d/cells grid))]
                [(:position cell) (boolean (cell/blocks-vision? cell))])]
    {:width width :height height :cells cells}))

(defn cell-at [world [x y]]
  (when-let [cell-atom ((:world/grid world) [x y])]
    @cell-atom))

(defn cells-at [world tile-positions]
  (for [[x y] tile-positions
        :let [cell (cell-at world [x y])]
        :when cell]
    [[x y] cell]))

(defn- create-grid [^TiledMap tiled-map]
  (let [props (.getProperties tiled-map)]
    (g2d/create (.get props "width")
                (.get props "height")
                (fn [position]
                  (atom
                   (cell/map->R
                    {:position position
                     :middle (mapv (partial + 0.5) position)
                     :movement (case (tiled-map/movement-property tiled-map position)
                                  "none" :none
                                  "air" :air
                                  "all" :all)
                     :entities #{}
                     :occupied #{}}))))))

(defn create [^TiledMap tiled-map]
  (let [props (.getProperties tiled-map)
        width (.get props "width")
        height (.get props "height")]
    {:world/id-counter (atom 0)
     :world/entity-ids (atom {})
     :world/grid (create-grid tiled-map)
     :world/content-grid (content-grid/create width height 16)}))

(ns moon.world
  (:require [moon.tiled-map :as tiled-map]
            [moon.cell :as cell]
            [moon.content-grid :as content-grid])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn cell-at [world [x y]]
  (when-let [cell-atom (get-in (:world/grid world) [x y])]
    @cell-atom))

(defn cells-at [world tile-positions]
  (for [[x y] tile-positions
        :let [cell (cell-at world [x y])]
        :when cell]
    [[x y] cell]))

(defn- create-grid [^TiledMap tiled-map]
  (let [props (.getProperties tiled-map)
        w (.get props "width")
        h (.get props "height")
        xyfn (fn [position]
               (atom
                (cell/map->R
                 {:position position
                  :middle (mapv (partial + 0.5) position)
                  :movement (case (tiled-map/movement-property tiled-map position)
                               "none" :none
                               "air" :air
                               "all" :all)
                  :entities #{}
                  :occupied #{}})))]
    (mapv (fn [x] (mapv (fn [y] (xyfn [x y]))
                        (range h)))
          (range w))))

(defn- create-raycaster [grid]
  (let [width (count grid)
        height (count (grid 0))
        arr (make-array Boolean/TYPE width height)]
    (doseq [cell (map deref (apply concat grid))]
      (let [[x y] (:position cell)]
        (aset arr x y (boolean (cell/blocks-vision? cell)))))
    [arr width height]))

(defn create
  [^TiledMap tiled-map {:keys [z-orders minimum-size]}]
  (let [props (.getProperties tiled-map)
        width (.get props "width")
        height (.get props "height")
        grid (create-grid tiled-map)]
    {:world/id-counter (atom 0)
     :world/entity-ids (atom {})
     :world/grid grid
     :world/content-grid (content-grid/create width height 16)
     :world/explored-tile-corners (atom
                                   (mapv (fn [x] (mapv (fn [y] false)
                                                       (range height)))
                                         (range width)))
     :world/raycaster (create-raycaster grid)
     :world/elapsed-time (atom 0)
     :world/z-orders z-orders
     :world/minimum-size minimum-size}))

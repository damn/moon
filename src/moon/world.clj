(ns moon.world
  (:require [moon.tiled-map :as tiled-map]
            [moon.body :as body]
            [moon.cell :as cell]
            [moon.content-grid :as content-grid]
            [moon.g2d :as g2d]
            [moon.grid :as grid]
            [moon.potential-field :as potential-field])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn register-eid! [world eid]
  (assert (and (not (contains? @eid :entity/id))))
  (let [id (swap! (:world/id-counter world) inc)]
    (assert (number? id))
    (swap! eid assoc :entity/id id)
    (swap! (:world/entity-ids world) assoc id eid))

  (assert (:entity/position @eid))
  (content-grid/update-entity! (:world/content-grid world) eid)

  (assert (:entity/position @eid))
  (when (:entity/collides? @eid)
    (assert (grid/valid-position? (:world/grid world) @eid (:entity/id @eid))))
  (grid/set-touched-cells! (:world/grid world) eid)
  (when (:entity/collides? @eid)
    (grid/set-occupied-cells! (:world/grid world) eid))
  nil)

(defn unregister-eid! [world eid]
  (let [id (:entity/id @eid)]
    (swap! (:world/entity-ids world) dissoc id)
    (content-grid/remove-entity! (:world/content-grid world) eid)
    (grid/remove-from-touched-cells! (:world/grid world) eid)
    (when (:entity/collides? @eid)
      (grid/remove-from-occupied-cells! (:world/grid world) eid)))
  nil)

(defn relocate-eid! [world eid]
  (content-grid/update-entity! (:world/content-grid world) eid)
  (grid/remove-from-touched-cells! (:world/grid world) eid)
  (grid/set-touched-cells! (:world/grid world) eid)
  (when (:entity/collides? @eid)
    (grid/remove-from-occupied-cells! (:world/grid world) eid)
    (grid/set-occupied-cells! (:world/grid world) eid))
  nil)

(defn try-move-solid-body [world body entity-id movement]
  (grid/try-move-solid-body (:world/grid world) body entity-id movement))

(defn nearest-enemy [world entity]
  (potential-field/nearest-enemy (:world/grid world) entity))

(defn nearest-enemy-distance [world entity]
  (potential-field/nearest-enemy-distance (:world/grid world) entity))

(defn find-direction [world eid]
  (potential-field/find-direction (:world/grid world) eid))

(defn point->entities [world position]
  (grid/point->entities (:world/grid world) position))

(defn circle->entities [world circle]
  (grid/circle->entities (:world/grid world) circle))

(defn- touched-tile-cells [world entity]
  (map deref (g2d/get-cells (:world/grid world) (body/touched-tiles entity))))

(defn entities-at-touched-tiles [world entity]
  (grid/entities (touched-tile-cells world entity)))

(defn blocked-at-touched-tiles? [world entity z-order]
  (some #(cell/blocked? % z-order) (touched-tile-cells world entity)))

(defn active-entities [world center-entity]
  (content-grid/active-entities (:world/content-grid world) center-entity))

(defn entity-by-id [world id]
  (get @(:world/entity-ids world) id))

(defn destroyed-eids [world]
  (filter (comp :entity/destroyed? deref) (vals @(:world/entity-ids world))))

(defn update-potential-fields! [world pf-cache faction entities max-iterations]
  (potential-field/update! (:world/grid world) pf-cache faction entities max-iterations))

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

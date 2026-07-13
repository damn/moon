(ns moon.world
  (:require [gdx.tiled-map :as tiled-map]
            [moon.body :as body]
            [moon.cell :as cell]
            [moon.content-grid :as content-grid]
            [moon.g2d :as g2d]
            [moon.grid :as grid]))

(defn- get-grid [world]
  (:world/grid world))

(defn- get-content-grid [world]
  (:world/content-grid world))

(defn- get-entity-ids [world]
  (:world/entity-ids world))

(defn register-eid! [world eid]
  (assert (and (not (contains? @eid :entity/id))))
  (let [id (swap! (:world/id-counter world) inc)]
    (assert (number? id))
    (swap! eid assoc :entity/id id)
    (swap! (get-entity-ids world) assoc id eid))

  (assert (:entity/body @eid))
  (content-grid/update-entity! (get-content-grid world) eid)

  (assert (:entity/body @eid))
  (when (:body/collides? (:entity/body @eid))
    (assert (grid/valid-position? (get-grid world) (:entity/body @eid) (:entity/id @eid))))
  (grid/set-touched-cells! (get-grid world) eid)
  (when (:body/collides? (:entity/body @eid))
    (grid/set-occupied-cells! (get-grid world) eid))
  nil)

(defn unregister-eid! [world eid]
  (let [id (:entity/id @eid)]
    (swap! (get-entity-ids world) dissoc id)
    (content-grid/remove-entity! (get-content-grid world) eid)
    (grid/remove-from-touched-cells! (get-grid world) eid)
    (when (:body/collides? (:entity/body @eid))
      (grid/remove-from-occupied-cells! (get-grid world) eid)))
  nil)

(defn relocate-eid! [world eid]
  (content-grid/update-entity! (get-content-grid world) eid)
  (grid/remove-from-touched-cells! (get-grid world) eid)
  (grid/set-touched-cells! (get-grid world) eid)
  (when (:body/collides? (:entity/body @eid))
    (grid/remove-from-occupied-cells! (get-grid world) eid)
    (grid/set-occupied-cells! (get-grid world) eid))
  nil)

(defn try-move-solid-body [world body entity-id movement]
  (grid/try-move-solid-body (get-grid world) body entity-id movement))

(defn nearest-enemy [world entity]
  (grid/nearest-enemy (get-grid world) entity))

(defn nearest-enemy-distance [world entity]
  (grid/nearest-enemy-distance (get-grid world) entity))

(defn find-direction [world eid]
  (grid/find-direction (get-grid world) eid))

(defn point->entities [world position]
  (grid/point->entities (get-grid world) position))

(defn circle->entities [world circle]
  (grid/circle->entities (get-grid world) circle))

(defn- touched-tile-cells [world entity]
  (map deref (g2d/get-cells (get-grid world) (body/touched-tiles (:entity/body entity)))))

(defn entities-at-touched-tiles [world entity]
  (grid/entities (touched-tile-cells world entity)))

(defn blocked-at-touched-tiles? [world entity z-order]
  (some #(cell/blocked? % z-order) (touched-tile-cells world entity)))

(defn active-entities [world center-entity]
  (content-grid/active-entities (get-content-grid world) center-entity))

(defn entity-by-id [world id]
  (get @(get-entity-ids world) id))

(defn destroyed-eids [world]
  (filter (comp :entity/destroyed? deref) (vals @(get-entity-ids world))))

(defn update-potential-fields! [world pf-cache faction entities max-iterations]
  (grid/update! (get-grid world) pf-cache faction entities max-iterations))

(defn raycaster-data [world]
  (let [grid (get-grid world)
        width (g2d/width grid)
        height (g2d/height grid)
        cells (for [cell (map deref (g2d/cells grid))]
                [(:position cell) (boolean (cell/blocks-vision? cell))])]
    {:width width :height height :cells cells}))

(defn cell-at [world [x y]]
  (when-let [cell-atom ((get-grid world) [x y])]
    @cell-atom))

(defn cells-at [world tile-positions]
  (for [[x y] tile-positions
        :let [cell (cell-at world [x y])]
        :when cell]
    [[x y] cell]))

(defn- create-grid [tiled-map]
  (g2d/create (tiled-map/get-property tiled-map "width")
              (tiled-map/get-property tiled-map "height")
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
                   :occupied #{}})))))

(defn create [tiled-map]
  (let [width (tiled-map/get-property tiled-map "width")
        height (tiled-map/get-property tiled-map "height")]
    {:world/id-counter (atom 0)
     :world/entity-ids (atom {})
     :world/grid (create-grid tiled-map)
     :world/content-grid (content-grid/create width height 16)}))

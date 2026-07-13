(ns moon.world
  (:require [gdx.tiled-map :as tiled-map]
            [moon.cell :as cell]
            [moon.content-grid :as content-grid]
            [moon.g2d :as g2d]
            [moon.grid :as grid]))

(defn get-grid [world]
  (:world/grid world))

(defn get-content-grid [world]
  (:world/content-grid world))

(defn get-entity-ids [world]
  (:world/entity-ids world))

(defn register-eid! [world eid]
  (assert (and (not (contains? @eid :entity/id))))
  (let [id (swap! (:world/id-counter world) inc)]
    (assert (number? id))
    (swap! eid assoc :entity/id id)
    (swap! (:world/entity-ids world) assoc id eid))

  (assert (:entity/body @eid))
  (content-grid/update-entity! (get-content-grid world) eid)

  (assert (:entity/body @eid))
  (when (:body/collides? (:entity/body @eid))
    (assert (grid/valid-position? (get-grid world) (:entity/body @eid) (:entity/id @eid))))
  (grid/set-touched-cells! (get-grid world) eid)
  (when (:body/collides? (:entity/body @eid))
    (grid/set-occupied-cells! (get-grid world) eid))
  nil)

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

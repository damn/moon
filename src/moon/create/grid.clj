(ns moon.create.grid
  (:require [moon.body :as body]
            [moon.cell :as cell]
            [moon.circle :as circle]
            [moon.faction :as faction]
            [moon.grid :as grid]
            [moon.grid2d :as g2d]
            [moon.position :as position]
            [moon.rectangle :as rectangle]
            [moon.tiled-map :as tiled-map])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)
           (com.badlogic.gdx.math Circle
                                  Intersector
                                  Rectangle)))

(defn- body->occupied-cells
  [grid
   {:keys [body/position
           body/width
           body/height]
    :as body}]
  (if (or (> (float width) 1) (> (float height) 1))
    (g2d/get-cells grid (body/touched-tiles body))
    [(grid (mapv int position))]))

(defn step [{:keys [^TiledMap ctx/tiled-map] :as ctx}]
  (assoc ctx :ctx/grid (g2d/create-grid (.get (.getProperties tiled-map) "width")
                                        (.get (.getProperties tiled-map) "height")
                                        (fn [position]
                                          (atom (cell/create position
                                                             (case (tiled-map/movement-property tiled-map position)
                                                               "none" :none
                                                               "air"  :air
                                                               "all"  :all)))))))

(extend-type moon.grid2d.VectorGrid
  grid/Grid
  (circle->cells [g2d circle]
    (->> circle
         circle/outer-rectangle
         rectangle/touched-tiles
         (g2d/get-cells g2d)))

  (circle->entities [g2d {:keys [position radius] :as circle}]
    (->> (grid/circle->cells g2d circle)
         (map deref)
         grid/cells->entities
         (filter #(Intersector/overlaps
                   (Circle. (position 0) (position 1) radius)
                   (body/rectangle (:entity/body @%))))))

  (cached-adjacent-cells [g2d cell]
    (if-let [result (:adjacent-cells @cell)]
      result
      (let [result (->> @cell
                        :position
                        position/get-8-neighbours
                        (g2d/get-cells g2d))]
        (swap! cell assoc :adjacent-cells result)
        result)))

  (point->entities [g2d position]
    (when-let [cell (g2d (mapv int position))]
      (let [[x y] position]
        (filter #(Rectangle/.contains (body/rectangle (:entity/body @%)) x y)
                (:entities @cell)))))

  (set-touched-cells! [grid eid]
    (let [cells (g2d/get-cells grid (body/touched-tiles (:entity/body @eid)))]
      (assert (not-any? nil? cells))
      (swap! eid assoc ::touched-cells cells) ; TODO :entity/touched-cells ....
      (doseq [cell cells]
        (assert (not (get (:entities @cell) eid)))
        (swap! cell update :entities conj eid))))

  (remove-from-touched-cells! [_ eid]
    (doseq [cell (::touched-cells @eid)]
      (assert (get (:entities @cell) eid))
      (swap! cell update :entities disj eid)))

  (set-occupied-cells! [grid eid]
    (let [cells (body->occupied-cells grid (:entity/body @eid))]
      (doseq [cell cells]
        (assert (not (get (:occupied @cell) eid)))
        (swap! cell update :occupied conj eid))
      (swap! eid assoc ::occupied-cells cells)))

  (remove-from-occupied-cells! [_ eid]
    (doseq [cell (::occupied-cells @eid)]
      (assert (get (:occupied @cell) eid))
      (swap! cell update :occupied disj eid)))

  ; TODO take entity ! some things not required @ body !?
  (valid-position? [g2d {:keys [body/z-order] :as body} entity-id]
    (assert (:body/collides? body))
    (let [cells* (into [] (map deref) (g2d/get-cells g2d (body/touched-tiles body)))]
      (and (not-any? #(cell/blocked? % z-order) cells*)
           (->> cells*
                grid/cells->entities
                (not-any? (fn [other-entity]
                            (let [other-entity @other-entity]
                              (and (not= (:entity/id other-entity) entity-id)
                                   (:body/collides? (:entity/body other-entity))
                                   (Intersector/overlaps (body/rectangle (:entity/body other-entity))
                                                         (body/rectangle body))))))))))

  (nearest-enemy-distance [grid entity]
    (cell/nearest-entity-distance @(grid (mapv int (:body/position (:entity/body entity))))
                                  (faction/enemy (:entity/faction entity))))

  (nearest-enemy [grid entity]
    (cell/nearest-entity @(grid (mapv int (:body/position (:entity/body entity))))
                         (faction/enemy (:entity/faction entity)))))

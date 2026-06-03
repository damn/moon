(ns moon.grid
  (:require [clojure.gdx.math.rectangle :as gdx-rectangle]
            [moon.body.overlaps :refer [overlaps?]]
            [moon.body.touched-tiles :refer [touched-tiles]]
            [moon.body.rectangle :refer [->rectangle]]
            [moon.cell :as cell]
            [moon.grid.cells-entities :as cells->entities]
            [moon.grid.body-occupied-cells :refer [body->occupied-cells]]
            [moon.faction :as faction]
            [clojure.grid2d.get-cells :refer [get-cells]]))

(defn point->entities [g2d pos]
  (when-let [cell (g2d (mapv int pos))]
    (filter #(gdx-rectangle/contains? (->rectangle (:entity/body @%)) pos)
            (:entities @cell))))

(defn set-touched-cells! [grid eid]
  (let [cells (get-cells grid (touched-tiles (:entity/body @eid)))]
    (assert (not-any? nil? cells))
    (swap! eid assoc ::touched-cells cells) ; TODO :entity/touched-cells ....
    (doseq [cell cells]
      (assert (not (get (:entities @cell) eid)))
      (swap! cell update :entities conj eid))))

(defn remove-from-touched-cells! [_ eid]
  (doseq [cell (::touched-cells @eid)]
    (assert (get (:entities @cell) eid))
    (swap! cell update :entities disj eid)))

(defn set-occupied-cells! [grid eid]
  (let [cells (body->occupied-cells grid (:entity/body @eid))]
    (doseq [cell cells]
      (assert (not (get (:occupied @cell) eid)))
      (swap! cell update :occupied conj eid))
    (swap! eid assoc ::occupied-cells cells)))

(defn remove-from-occupied-cells! [_ eid]
  (doseq [cell (::occupied-cells @eid)]
    (assert (get (:occupied @cell) eid))
    (swap! cell update :occupied disj eid)))

  ; TODO take entity ! some things not required @ body !?
(defn valid-position? [g2d {:keys [body/z-order] :as body} entity-id]
  (assert (:body/collides? body))
  (let [cells* (into [] (map deref) (get-cells g2d (touched-tiles body)))]
    (and (not-any? #(cell/blocked? % z-order) cells*)
         (->> cells*
              cells->entities/f
              (not-any? (fn [other-entity]
                          (let [other-entity @other-entity]
                            (and (not= (:entity/id other-entity) entity-id)
                                 (:body/collides? (:entity/body other-entity))
                                 (overlaps? (:entity/body other-entity)
                                                 body)))))))))

(defn nearest-enemy-distance [grid entity]
  (cell/nearest-entity-distance @(grid (mapv int (:body/position (:entity/body entity))))
                                (faction/enemy (:entity/faction entity))))

(defn nearest-enemy [grid entity]
  (cell/nearest-entity @(grid (mapv int (:body/position (:entity/body entity))))
                       (faction/enemy (:entity/faction entity))))

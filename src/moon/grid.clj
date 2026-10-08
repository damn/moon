(ns moon.grid
  (:require [clojure.math :as math]
            [moon.body :as body]
            [moon.cell :as cell]
            [moon.circle :as moon-circle]
            [moon.g2d :as g2d]
            [moon.rectangle :as rectangle]
            [moon.v2 :as v2])
  (:import (com.badlogic.gdx.math Circle Intersector Rectangle)))

(defn entities [cells]
  (into #{} (mapcat :entities) cells))

(defn valid-position? [g2d {:keys [entity/z-order] :as body} entity-id]
  (assert (:entity/collides? body))
  (let [cells* (map deref (keep g2d (body/touched-tiles body)))]
    (and (not-any? #(cell/blocked? % z-order) cells*)
         (->> cells*
              entities
              (not-any? (fn [other-entity]
                          (let [other-entity @other-entity]
                            (and (not= (:entity/id other-entity) entity-id)
                                 (:entity/collides? other-entity)
                                 (body/overlaps? other-entity
                                                 body)))))))))

(defn try-move [grid body entity-id movement]
  (let [new-body (update body :entity/position v2/move movement)]
    (when (valid-position? grid new-body entity-id)
      new-body)))

(defn try-move-solid-body [grid body entity-id {[vx vy] :direction :as movement}]
  (let [xdir (math/signum (float vx))
        ydir (math/signum (float vy))]
    (or (try-move grid body entity-id movement)
        (try-move grid body entity-id (assoc movement :direction [xdir 0]))
        (try-move grid body entity-id (assoc movement :direction [0 ydir])))))

(defn body->occupied-cells
  [grid {:keys [entity/position
                entity/width
                entity/height]
         :as body}]
  (if (or (> (float width) 1) (> (float height) 1))
    (keep grid (body/touched-tiles body))
    [(grid (mapv int position))]))

(defn set-occupied-cells! [grid eid]
  (let [cells (body->occupied-cells grid @eid)]
    (doseq [cell cells]
      (assert (not (get (:occupied @cell) eid)))
      (swap! cell update :occupied conj eid))
    (swap! eid assoc :entity/occupied-cells cells)))

(defn set-touched-cells! [grid eid]
  (let [cells (keep grid (body/touched-tiles @eid))]
    (assert (not-any? nil? cells))
    (swap! eid assoc :entity/touched-cells cells)
    (doseq [cell cells]
      (assert (not (get (:entities @cell) eid)))
      (swap! cell update :entities conj eid))))

(defn remove-from-occupied-cells! [_ eid]
  (doseq [cell (:entity/occupied-cells @eid)]
    (assert (get (:occupied @cell) eid))
    (swap! cell update :occupied disj eid)))

(defn remove-from-touched-cells! [_ eid]
  (doseq [cell (:entity/touched-cells @eid)]
    (assert (get (:entities @cell) eid))
    (swap! cell update :entities disj eid)))

(defn point->entities [g2d pos]
  (when-let [cell (g2d (mapv int pos))]
    (filter #(.contains ^Rectangle (body/rectangle @%) (float (first pos)) (float (second pos)))
            (:entities @cell))))

(defn circle->entities [g2d {:keys [position radius] :as circle}]
  (let [[x y] position
        gdx-circle (Circle. (float x) (float y) (float radius))]
    (->> circle
         moon-circle/outer-rectangle
         rectangle/touched-tiles
         (keep g2d)
         (map deref)
         entities
         (filter #(Intersector/overlaps ^Circle gdx-circle
                                        ^Rectangle (body/rectangle @%))))))

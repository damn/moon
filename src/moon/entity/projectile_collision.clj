(ns moon.entity.projectile-collision
  (:require [clojure.grid2d :as g2d]
            [moon.body :as body]
            [moon.entity :as entity]
            [moon.world.grid :as grid]
            [moon.world.grid.cell :as cell]))

(defmethod entity/create :entity/projectile-collision
  [[_ v] _ctx]
  (assoc v :already-hit-bodies #{}))

(defmethod entity/tick :entity/projectile-collision
  [[_k {:keys [entity-effects already-hit-bodies piercing?]}]
   eid
   {:keys [ctx/world]}]
  (let [grid (:world/grid world)
        entity @eid
        cells* (map deref (g2d/get-cells grid (body/touched-tiles (:entity/body entity))))
        hit-entity (first (filter #(and (not (contains? already-hit-bodies %))
                                        (not= (:entity/faction entity)
                                              (:entity/faction @%))
                                        (:body/collides? (:entity/body @%))
                                        (body/overlaps? (:entity/body entity)
                                                        (:entity/body @%)))
                                  (grid/cells->entities cells*)))
        destroy? (or (and hit-entity (not piercing?))
                     (some #(cell/blocked? % (:body/z-order (:entity/body entity))) cells*))]
    [(when destroy?
       [:tx/mark-destroyed eid])
     (when hit-entity
       [:tx/assoc-in
        eid
        [:entity/projectile-collision
         :already-hit-bodies]
        (conj already-hit-bodies hit-entity)])
     (when hit-entity
       [:tx/effect
        {:effect/source eid
         :effect/target hit-entity}
        entity-effects])]))

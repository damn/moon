(ns entity.tick.projectile-collision
  (:require [moon.body :as body]
            [moon.cell :as cell]
            [moon.grid.cells-entities :as cells->entities]
            [clojure.grid2d.get-cells :refer [get-cells]]))

(defn f
  [{:keys [entity-effects already-hit-bodies piercing?]}
   eid
   {:keys [ctx/grid]}]
  (let [entity @eid
        cells* (map deref (get-cells grid (body/touched-tiles (:entity/body entity))))
        hit-entity (first (filter #(and (not (contains? already-hit-bodies %))
                                        (not= (:entity/faction entity)
                                              (:entity/faction @%))
                                        (:body/collides? (:entity/body @%))
                                        (body/overlaps? (:entity/body entity)
                                                        (:entity/body @%)))
                                  (cells->entities/f cells*)))
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

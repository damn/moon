(ns ctx.entity.tick.npc-idle.create-effect-ctx
  (:require [moon.body.direction :as direction]
            [moon.grid.nearest-enemy :refer [nearest-enemy]]
            [moon.raycaster.line-of-sight :as line-of-sight?]))

(defn f
  [{:keys [ctx/grid
           ctx/raycaster]}
   eid]
  (let [entity @eid
        target (nearest-enemy grid entity)
        target (when (and target
                          (line-of-sight?/f raycaster entity @target))
                 target)]
    {:effect/source eid
     :effect/target target
     :effect/target-direction (when target
                                (direction/f (:entity/body entity)
                                             (:entity/body @target)))}))

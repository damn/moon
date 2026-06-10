(ns entity.tick.npc-idle.create-effect-ctx
  (:require [moon.body :as body]
            [moon.grid.nearest-enemy :refer [nearest-enemy]]
            [moon.raycaster :as raycaster]))

(defn f
  [{:keys [ctx/grid
           ctx/raycaster]}
   eid]
  (let [entity @eid
        target (nearest-enemy grid entity)
        target (when (and target
                          (raycaster/line-of-sight? raycaster entity @target))
                 target)]
    {:effect/source eid
     :effect/target target
     :effect/target-direction (when target
                                (body/direction (:entity/body entity)
                                                (:entity/body @target)))}))

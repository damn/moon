(ns clojure.moon.npc-effect-ctx
  (:require [moon.body :as body]
            [clojure.nearest-enemy :refer [nearest-enemy]]
            [clojure.line-of-sight :as line-of-sight?]))

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
                                (body/direction (:entity/body entity)
                                                (:entity/body @target)))}))

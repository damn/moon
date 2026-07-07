(ns ctx.effects.useful.projectile
  (:require [clojure.distance :as distance]
            [clojure.double-ray-endpositions :as double-ray-endpositions]
            [moon.raycaster.is-blocked :as blocked?]))

(defn f
  [[_ {:keys [projectile/max-range] :as projectile}]
   {:keys [effect/source effect/target]}
   {:keys [ctx/raycaster]}]
  ; TODO valid params direction has to be  non-nil (entities not los player ) ?
  (let [source-p (:body/position (:entity/body @source))
        target-p (:body/position (:entity/body @target))]
    ; is path blocked ereally needed? we need LOS also right to have a target-direction as AI?
    (and (not (let [[start1,target1,start2,target2] (double-ray-endpositions/f source-p
                                                                               target-p
                                                                               (:projectile/size projectile))]
                (or
                 (blocked?/f raycaster start1 target1)
                 (blocked?/f raycaster start2 target2))))
         ; TODO not taking into account body sizes
         (< (distance/f source-p ; entity/distance function protocol EntityPosition
                        target-p)
            max-range))))

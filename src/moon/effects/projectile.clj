(ns moon.effects.projectile
  (:require [clojure.math.vector2 :as v]
            [moon.effect :as effect]
            [moon.world :as world]))

(defn- proj-start-point [body direction size]
  (v/add (:body/position body)
         (v/scale direction
                  (+ (/ (:body/width body) 2) size 0.1))))

(defmethod effect/applicable? :effects/projectile
  [_ {:keys [effect/target-direction]}]
  ; TODO for npcs need target -- anyway only with direction
  ; faction @ source also ?
  target-direction)

(defmethod effect/useful? :effects/projectile
  [[_ {:keys [projectile/max-range] :as projectile}]
   {:keys [effect/source effect/target]}
   world]
  ; TODO valid params direction has to be  non-nil (entities not los player ) ?
  (let [source-p (:body/position (:entity/body @source))
        target-p (:body/position (:entity/body @target))]
    ; is path blocked ereally needed? we need LOS also right to have a target-direction as AI?
    (and (not (world/path-blocked? world source-p target-p (:projectile/size projectile)))
         ; TODO not taking into account body sizes
         (< (v/distance source-p ; entity/distance function protocol EntityPosition
                        target-p)
            max-range))))

(defmethod effect/handle :effects/projectile
  [[_ projectile] {:keys [effect/source effect/target-direction]} _ctx]
  [[:tx/spawn-projectile
    {:position (proj-start-point (:entity/body @source)
                                 target-direction
                                 (:projectile/size projectile))
     :direction target-direction
     :faction (:entity/faction @source)}
    projectile]])

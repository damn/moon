(ns effects.useful.projectile
  (:require [clojure.math.vector2.normal-vectors :as normal-vectors]
            [clojure.math.vector2.add :as add]
            [clojure.math.vector2.direction :as direction]
            [clojure.math.vector2.distance :as distance]
            [clojure.math.vector2.scale :as scale]
            [moon.raycaster.is-blocked :as blocked?]))

(defn- create-double-ray-endpositions
  [[start-x start-y]
   [target-x target-y]
   path-w]
  {:pre [(< path-w 0.98)]} ; wieso 0.98??
  (let [path-w (+ path-w 0.02) ;etwas gr�sser damit z.b. projektil nicht an ecken anst�sst
        v (direction/f [start-x start-y]
                       [target-y target-y])
        [normal1 normal2] (normal-vectors/f v)
        normal1 (scale/f normal1 (/ path-w 2))
        normal2 (scale/f normal2 (/ path-w 2))
        start1  (add/f [start-x  start-y]  normal1)
        start2  (add/f [start-x  start-y]  normal2)
        target1 (add/f [target-x target-y] normal1)
        target2 (add/f [target-x target-y] normal2)]
    [start1,target1,start2,target2]))

(defn f
  [[_ {:keys [projectile/max-range] :as projectile}]
   {:keys [effect/source effect/target]}
   {:keys [ctx/raycaster]}]
  ; TODO valid params direction has to be  non-nil (entities not los player ) ?
  (let [source-p (:body/position (:entity/body @source))
        target-p (:body/position (:entity/body @target))]
    ; is path blocked ereally needed? we need LOS also right to have a target-direction as AI?
    (and (not (let [[start1,target1,start2,target2] (create-double-ray-endpositions source-p target-p (:projectile/size projectile))]
                (or
                 (blocked?/f raycaster start1 target1)
                 (blocked?/f raycaster start2 target2))))
         ; TODO not taking into account body sizes
         (< (distance/f source-p ; entity/distance function protocol EntityPosition
                        target-p)
            max-range))))

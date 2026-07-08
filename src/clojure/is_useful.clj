(ns clojure.is-useful
  (:require [clojure.v2.distance :as distance]
            [clojure.double-ray-endpositions :as double-ray-endpositions]
            [clojure.in-range :refer [in-range?]]
            [clojure.raycaster-is-blocked :as blocked?]))

(defmulti f
  (fn [[k _v] _effect-ctx _ctx]
    k))

(defmethod f :default
  [_ _effect-ctx _ctx]
  true)

(defmethod f :effects/audiovisual
  [_ _effect-ctx _ctx]
  false)

(defmethod f :effects/projectile
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

(defmethod f :effects/target-all
  [_ _effect-ctx _ctx]
  false)

(defmethod f :effects/target-entity
  [[_ {:keys [maxrange]}] {:keys [effect/source effect/target]} _ctx]
  (in-range? (:entity/body @source)
             (:entity/body @target)
             maxrange))

(defmethod f :effects.target/audiovisual
  [_ _effect-ctx _ctx]
  false)

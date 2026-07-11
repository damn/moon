(ns clojure.is-useful
  (:require [moon.v2 :as v2]
            [moon.body :as body]
            [clojure.raycaster :as raycaster]))

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
    (and (not (let [[start1,target1,start2,target2] (v2/double-ray-endpositions source-p
                                                                               target-p
                                                                               (:projectile/size projectile))]
                (or
                 (raycaster/blocked? raycaster start1 target1)
                 (raycaster/blocked? raycaster start2 target2))))
         ; TODO not taking into account body sizes
         (< (v2/distance source-p ; entity/distance function protocol EntityPosition
                        target-p)
            max-range))))

(defmethod f :effects/target-all
  [_ _effect-ctx _ctx]
  false)

(defmethod f :effects/target-entity
  [[_ {:keys [maxrange]}] {:keys [effect/source effect/target]} _ctx]
  (body/in-range? (:entity/body @source)
                  (:entity/body @target)
                  maxrange))

(defmethod f :effects.target/audiovisual
  [_ _effect-ctx _ctx]
  false)

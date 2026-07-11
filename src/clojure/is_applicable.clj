(ns clojure.is-applicable
  (:require [clojure.stats.melee-damage :as melee-damage]
            [moon.faction :as faction]))

(defmulti f
  (fn [[k _v] _effect-ctx]
    k))

(defmethod f :effects/audiovisual
  [_ {:keys [effect/target-position]}]
  target-position)

(defmethod f :effects/projectile
  [_ {:keys [effect/target-direction]}]
  ; TODO for npcs need target -- anyway only with direction
  ; faction @ source also ?
  target-direction)

(defmethod f :effects/spawn
  [_ {:keys [effect/source effect/target-position]}]
  (and (:entity/faction @source)
       target-position))

(defmethod f :effects/target-all
  [_ _] ; TODO check ..
  true)

(defmethod f :effects/target-entity
  [[_ {:keys [entity-effects]}] {:keys [effect/target] :as effect-ctx}]
  (and target
       (seq (filter #(f % effect-ctx) entity-effects))))

(defmethod f :effects.target/audiovisual
  [_ {:keys [effect/target]}]
  target)

(defmethod f :effects.target/convert
  [_ {:keys [effect/source effect/target]}]
  (and target
       (= (:entity/faction @target)
          (faction/enemy (:entity/faction @source)))))

(defmethod f :effects.target/damage
  [_ {:keys [effect/target]}]
  (and target
       #_(:stats/hp @target))) ; not exist anymore ... bugfix .... -> is 'creature?'

(defmethod f :effects.target/kill
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

(defmethod f :effects.target/melee-damage
  [_ {:keys [effect/source] :as effect-ctx}]
  ; TODO AT EFFECT CREATION MAKE
  ; same @ handle
  (f [:effects.target/damage (melee-damage/f @source)] effect-ctx))

(defmethod f :effects.target/spiderweb
  [_ {:keys [effect/target]}]
  ; TODO has stats , for mod-add
  ; e,g, spiderweb on projectile leads to error
  (:entity/stats @target))

(defmethod f :effects.target/stun
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

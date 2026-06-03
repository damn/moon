(ns effect.load
  (:require [clojure.math.vector2 :as v]
            [game.effect :as effect]
            [moon.faction :as faction]
            [game.constants :refer [spiderweb-modifiers spiderweb-duration]]
            [moon.raycaster :as raycaster]
            [moon.stats :as stats]
            [clojure.timer.create :refer [create-timer]]))

(defmethod effect/applicable? :effects.target/convert
  [_ {:keys [effect/source effect/target]}]
  (and target
       (= (:entity/faction @target)
          (faction/enemy (:entity/faction @source)))))

(defmethod effect/handle :effects.target/convert
  [_ {:keys [effect/source effect/target]} _ctx]
  [[:tx/assoc target :entity/faction (:entity/faction @source)]])

(defmethod effect/applicable? :effects.target/kill
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

(defmethod effect/handle :effects.target/kill
  [_ {:keys [effect/target]} _ctx]
  [[:tx/event target :kill]])

(defmethod effect/applicable? :effects.target/spiderweb
  [_ {:keys [effect/target]}]
  ; TODO has stats , for mod-add
  ; e,g, spiderweb on projectile leads to error
  (:entity/stats @target))

(defmethod effect/handle :effects.target/spiderweb
  [_ {:keys [effect/target]} {:keys [ctx/elapsed-time]}]
  ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
  (when-not (:entity/temp-modifier @target)
    [[:tx/assoc target :entity/temp-modifier {:modifiers spiderweb-modifiers
                                              :counter (create-timer elapsed-time spiderweb-duration)}]
     [:tx/update target :entity/stats stats/add spiderweb-modifiers]]))

(defmethod effect/applicable? :effects.target/stun
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

(defmethod effect/handle :effects.target/stun
  [[_ duration] {:keys [effect/target]} _ctx]
  [[:tx/event target :stun duration]])

(defmethod effect/applicable? :effects/audiovisual
  [_ {:keys [effect/target-position]}]
  target-position)

(defmethod effect/useful? :effects/audiovisual
  [_ _effect-ctx _ctx]
  false)

(defmethod effect/handle :effects/audiovisual
  [[_ audiovisual] {:keys [effect/target-position]} _ctx]
  [[:tx/audiovisual target-position audiovisual]])

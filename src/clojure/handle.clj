(ns clojure.handle
  (:require [clojure.stats.add-mods :as add-mods]
            [clojure.calc-damage :as calc-damage]
            [clojure.creature-melee-damage :as melee-damage]
            [clojure.effective-armor-save :as effective-armor-save]
            [clojure.end-point :refer [end-point]]
            [clojure.get-hitpoints :as get-hitpoints]
            [clojure.in-range :refer [in-range?]]
            [clojure.int-between :refer [rand-int-between]]
            [clojure.moon-target-all :as target-all]
            [clojure.projectile-start-point :as projectile-start-point]
            [clojure.start-point :refer [start-point]]
            [clojure.timer-create :refer [create-timer]]))

(def spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def spiderweb-duration 5)

(defmulti f
  (fn [[k _v] _effect-ctx _ctx]
    k))

(defmethod f :effects/audiovisual
  [[_ audiovisual] {:keys [effect/target-position]} _ctx]
  [[:tx/audiovisual target-position audiovisual]])

(defmethod f :effects/projectile
  [[_ projectile] {:keys [effect/source effect/target-direction]} _ctx]
  [[:tx/spawn-projectile
    {:position (projectile-start-point/f (:entity/body @source)
                                         target-direction
                                         (:projectile/size projectile))
     :direction target-direction
     :faction (:entity/faction @source)}
    projectile]])

(defmethod f :effects/spawn
  [[_ {:keys [property/id] :as property}]
   {:keys [effect/source effect/target-position]}
   _ctx]
  [[:tx/spawn-creature {:position target-position
                        :creature-property property
                        :components {:entity/fsm {:fsm :fsms/npc
                                                  :initial-state :npc-idle}
                                     :entity/faction (:entity/faction @source)}}]])

(defmethod f :effects/target-all
  [[_ {:keys [entity-effects]}]
   {:keys [effect/source]}
   {:keys [ctx/active-entities
           ctx/colors
           ctx/raycaster]}]
  (let [source* @source]
    (apply concat
           (for [target (target-all/affected-targets active-entities raycaster source*)]
             [[:tx/spawn-line
               {:start (:body/position (:entity/body source*)) #_(start-point source* target*)
                :end (:body/position (:entity/body @target))
                :duration 0.05
                :color (:colors/target-all-line colors)
                :thick? true}]
              [:tx/effect
               {:effect/source source
                :effect/target target}
               entity-effects]]))))

(defmethod f :effects/target-entity
  [[_ {:keys [maxrange entity-effects]}]
   {:keys [effect/source effect/target] :as effect-ctx}
   {:keys [ctx/colors]}]
  (let [body        (:entity/body @source)
        target-body (:entity/body @target)]
    (if (in-range? body target-body maxrange)
      [[:tx/spawn-line {:start (start-point body target-body)
                        :end (:body/position target-body)
                        :duration 0.05
                        :color (:colors/target-entity-line colors)
                        :thick? true}]
       [:tx/effect effect-ctx entity-effects]]
      [[:tx/audiovisual
        (end-point body target-body maxrange)
        :audiovisuals/hit-ground]])))

(defmethod f :effects.target/audiovisual
  [[_ audiovisual] {:keys [effect/target]} _ctx]
  [[:tx/audiovisual (:body/position (:entity/body @target)) audiovisual]])

(defmethod f :effects.target/convert
  [_ {:keys [effect/source effect/target]} _ctx]
  [[:tx/assoc target :entity/faction (:entity/faction @source)]])

(defmethod f :effects.target/damage
  [[_ damage] {:keys [effect/source effect/target]} _ctx]
  (let [source* @source
        target* @target
        hp (get-hitpoints/f (:entity/stats target*))]
    (cond
     (zero? (hp 0))
     nil

     ; TODO find a better way
     (not (:entity/stats target*))
     nil

     (and (:entity/stats source*)
          (:entity/stats target*)
          (< (rand) (effective-armor-save/f (:entity/stats source*)
                                            (:entity/stats target*))))
     [[:tx/add-text-effect target "[WHITE]ARMOR" 0.3]]

     :else
     (let [min-max (if (:entity/stats source*)  ; projectiles dont have ....
                     (:damage/min-max (calc-damage/f (:entity/stats source*)
                                                     (:entity/stats target*)
                                                     damage))
                     (:damage/min-max damage))
           dmg-amount (rand-int-between min-max)
           new-hp-val (max (- (hp 0) dmg-amount)
                           0)]
       [[:tx/assoc-in target [:entity/stats :stats/hp 0] new-hp-val]
        [:tx/event    target (if (zero? new-hp-val) :kill :alert)]
        [:tx/audiovisual (:body/position (:entity/body target*)) :audiovisuals/damage]
        [:tx/add-text-effect target (str "[RED]" dmg-amount "[]") 0.3]]))))

(defmethod f :effects.target/kill
  [_ {:keys [effect/target]} _ctx]
  [[:tx/event target :kill]])

(defmethod f :effects.target/melee-damage
  [_ {:keys [effect/source] :as effect-ctx} ctx]
  ; TODO AT EFFECT CREATION MAKE
  ; same @ applicable
  (f [:effects.target/damage (melee-damage/f @source)] effect-ctx ctx))

(defmethod f :effects.target/spiderweb
  [_ {:keys [effect/target]} {:keys [ctx/elapsed-time]}]
  ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
  (when-not (:entity/temp-modifier @target)
    [[:tx/assoc target :entity/temp-modifier {:modifiers spiderweb-modifiers
                                              :counter (create-timer elapsed-time spiderweb-duration)}]
     [:tx/update target :entity/stats add-mods/f spiderweb-modifiers]]))

(defmethod f :effects.target/stun
  [[_ duration] {:keys [effect/target]} _ctx]
  [[:tx/event target :stun duration]])

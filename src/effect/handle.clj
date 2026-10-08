(ns effect.handle
  (:require [entity.spawn :refer [spawn-entity!]]
            [game.audio :refer [audiovisual!]]
            [game.entity :refer [add-text-effect]]
            [game.fsm :refer [handle-fsm-event!]]
            [game.spawn :refer [spawn-creature spawn-line spawn-projectile]]
            [game.target-all :refer [affected-targets]]
            [moon.body :as body]
            [moon.rand :as rand]
            [moon.stats :as stats]
            [moon.timer :as timer]
            [moon.v2 :as v2]))

(def ^:private spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def ^:private spiderweb-duration 5)

(defn handle-effect
  [db world elapsed-time audio skin stage textures z-orders minimum-size
   [k v] effect-ctx world-mouse-position apply-effects!
   active-entities colors raycaster]
  (let [elapsed-time* @elapsed-time]
    (case k
      :effects/audiovisual
      (audiovisual! db world elapsed-time audio skin stage textures z-orders minimum-size (:effect/target-position effect-ctx) v)

      :effects/projectile
      (let [source (:effect/source effect-ctx)
            source* @source
            direction (:effect/target-direction effect-ctx)
            size (:projectile/size v)]
        (spawn-entity! world elapsed-time z-orders minimum-size
                       (spawn-projectile
                        {:position (mapv + (:entity/position source*)
                                         (v2/scale direction
                                                   (+ (/ (:entity/width source*) 2) size 0.1)))
                         :direction direction
                         :faction (:entity/faction source*)}
                        v)))

      :effects/spawn
      (let [source (:effect/source effect-ctx)]
        (spawn-entity! world elapsed-time z-orders minimum-size
                       (spawn-creature {:position (:effect/target-position effect-ctx)
                                        :creature-property v
                                        :components {:entity/fsm {:fsm :fsms/npc
                                                                  :initial-state :npc-idle}
                                                     :entity/faction (:entity/faction @source)}})))

      :effects/target-all
      (let [source (:effect/source effect-ctx)
            source* @source]
        (doseq [target (affected-targets active-entities raycaster source*)]
          (spawn-entity! world elapsed-time z-orders minimum-size
                         (spawn-line
                          {:start (:entity/position source*)
                           :end (:entity/position @target)
                           :duration 0.05
                           :color (:colors/target-all-line colors)
                           :thick? true}))
          (apply-effects! {:effect/source source
                           :effect/target target}
                          (:entity-effects v))))

      :effects/target-entity
      (let [source (:effect/source effect-ctx)
            target (:effect/target effect-ctx)
            body        @source
            target-body @target
            {:keys [maxrange entity-effects]} v]
        (if (body/in-range? body target-body maxrange)
          (do (spawn-entity! world elapsed-time z-orders minimum-size
                             (spawn-line
                              {:start (body/start-point body target-body)
                               :end (:entity/position target-body)
                               :duration 0.05
                               :color (:colors/target-entity-line colors)
                               :thick? true}))
              (apply-effects! effect-ctx entity-effects))
          (audiovisual! db world elapsed-time audio skin stage textures z-orders minimum-size
                        (body/end-point body target-body maxrange)
                        :audiovisuals/hit-ground)))

      :effects.target/audiovisual
      (audiovisual! db world elapsed-time audio skin stage textures z-orders minimum-size (:entity/position @(:effect/target effect-ctx)) v)

      :effects.target/convert
      (let [source (:effect/source effect-ctx)
            target (:effect/target effect-ctx)]
        (swap! target assoc :entity/faction (:entity/faction @source))
        nil)

      :effects.target/damage
      (let [source (:effect/source effect-ctx)
            target (:effect/target effect-ctx)
            source* @source
            target* @target
            hp (stats/get-hitpoints (:entity/stats target*))]
        (cond
         (zero? (hp 0))
         nil

         ; TODO find a better way
         (not (:entity/stats target*))
         nil

         (and (:entity/stats source*)
              (:entity/stats target*)
              (< (rand) (stats/effective-armor-save (:entity/stats source*)
                                                    (:entity/stats target*))))
         (do (swap! target add-text-effect elapsed-time* "[WHITE]ARMOR" 0.3)
             nil)

         :else
         (let [min-max (if (:entity/stats source*)  ; projectiles dont have ....
                         (:damage/min-max (stats/calc-damage (:entity/stats source*)
                                                             (:entity/stats target*)
                                                             v))
                         (:damage/min-max v))
               dmg-amount (rand/int-between min-max)
               new-hp-val (max (- (hp 0) dmg-amount)
                               0)
               dmg-text (str "[RED]" dmg-amount "[]")]
           (swap! target assoc-in [:entity/stats :stats/hp 0] new-hp-val)
           (swap! target add-text-effect elapsed-time* dmg-text 0.3)
           (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size target world-mouse-position (if (zero? new-hp-val) :kill :alert))
           (audiovisual! db world elapsed-time audio skin stage textures z-orders minimum-size (:entity/position target*) :audiovisuals/damage))))

      :effects.target/kill
      (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size (:effect/target effect-ctx) world-mouse-position :kill)

      :effects.target/melee-damage
      ; TODO AT EFFECT CREATION MAKE
      ; same @ applicable
      (handle-effect db world elapsed-time audio skin stage textures z-orders minimum-size
                     [:effects.target/damage (stats/melee-damage @(:effect/source effect-ctx))]
                     effect-ctx
                     world-mouse-position
                     apply-effects!
                     active-entities
                     colors
                     raycaster)

      :effects.target/spiderweb
      (let [target (:effect/target effect-ctx)]
        ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
        (when-not (:entity/temp-modifier @target)
          (swap! target assoc :entity/temp-modifier {:modifiers spiderweb-modifiers
                                                     :counter (timer/create elapsed-time* spiderweb-duration)})
          (swap! target update :entity/stats stats/add-mods spiderweb-modifiers)
          nil))

      :effects.target/stun
      (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size (:effect/target effect-ctx) world-mouse-position :stun v))))

(ns moon.effect
  (:require [clojure.math.vector2 :as v]
            [clojure.rand :refer [rand-int-between]]
            [moon.effects.target-all :as target-all]
            [moon.effects.target-entity :as target-entity]
            [moon.entity.stats :as stats]
            [moon.faction :as faction]
            [moon.timer :as timer]
            [moon.world.raycaster :as raycaster]))

(defn- proj-start-point [body direction size]
  (v/add (:body/position body)
         (v/scale direction
                  (+ (/ (:body/width body) 2) size 0.1))))

(def ^:private spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def ^:private spiderweb-duration 5)

(defn- entity->melee-damage [{:keys [entity/stats]}]
  (let [strength (or (stats/get-stat-value stats :stats/strength) 0)]
    {:damage/min-max [strength strength]}))

(defn- melee-damage-effect [entity]
  [:effects.target/damage (entity->melee-damage entity)])

; not in stats because projectile as source doesnt have stats
; FIXME I don't see it triggering with 10 armor save ... !
(defn- effective-armor-save [source-stats target-stats]
  (max (- (or (stats/get-stat-value source-stats :stats/armor-save)   0)
          (or (stats/get-stat-value target-stats :stats/armor-pierce) 0))
       0))

(comment

 (effective-armor-save {} {:stats/modifiers {:modifiers/armor-save {:op/inc 10}}
                           :stats/armor-save 0})
 ; broken
 (let [source* {:stats/armor-pierce 0.4}
       target* {:stats/armor-save   0.5}]
   (effective-armor-save source* target*))
 )

(defn calc-damage
  ([source target damage]
   (update (calc-damage source damage)
           :damage/min-max
           moon.entity.stats/apply-max
           (:stats/modifiers target)
           :modifier/damage-receive-max))
  ([source damage]
   (update damage
           :damage/min-max
           #(-> %
                (moon.entity.stats/apply-min (:stats/modifiers source) :modifier/damage-deal-min)
                (moon.entity.stats/apply-max (:stats/modifiers source) :modifier/damage-deal-max)))))

(declare applicable?
         handle)

(def ^:private k->fn
  {:effects/audiovisual {:applicable? (fn [_ {:keys [effect/target-position]}]
                                        target-position)

                         :useful?     (fn [_ _effect-ctx _world]
                                        false)

                         :handle      (fn [[_ audiovisual] {:keys [effect/target-position]} _world]
                                        [[:tx/audiovisual target-position audiovisual]])}

   :effects/projectile {:applicable? (fn applicable? [_ {:keys [effect/target-direction]}]
                                       ; TODO for npcs need target -- anyway only with direction
                                       ; faction @ source also ?
                                       target-direction)
                        :useful?     (fn [[_ {:keys [projectile/max-range] :as projectile}]
                                          {:keys [effect/source effect/target]}
                                          world]
                                       ; TODO valid params direction has to be  non-nil (entities not los player ) ?
                                       (let [source-p (:body/position (:entity/body @source))
                                             target-p (:body/position (:entity/body @target))]
                                         ; is path blocked ereally needed? we need LOS also right to have a target-direction as AI?
                                         (and (not (raycaster/path-blocked? world source-p target-p (:projectile/size projectile)))
                                              ; TODO not taking into account body sizes
                                              (< (v/distance source-p ; entity/distance function protocol EntityPosition
                                                             target-p)
                                                 max-range))))
                        :handle      (fn [[_ projectile] {:keys [effect/source effect/target-direction]} _world]
                                       [[:tx/spawn-projectile
                                         {:position (proj-start-point (:entity/body @source)
                                                                      target-direction
                                                                      (:projectile/size projectile))
                                          :direction target-direction
                                          :faction (:entity/faction @source)}
                                         projectile]])}

   :effects/spawn {:applicable? (fn [_ {:keys [effect/source effect/target-position]}]
                                  (and (:entity/faction @source)
                                       target-position))
                   :handle      (fn [[_ {:keys [property/id] :as property}]
                                     {:keys [effect/source effect/target-position]}
                                     _world]
                                  [[:tx/spawn-creature {:position target-position
                                                        :creature-property property
                                                        :components {:entity/fsm {:fsm :fsms/npc
                                                                                  :initial-state :npc-idle}
                                                                     :entity/faction (:entity/faction @source)}}]])}

   :effects/target-all {:applicable? (fn  [_ _] ; TODO check ..
                                       true)
                        :useful?     (fn [_ _effect-ctx _world]
                                       false)
                        :handle      (fn [[_ {:keys [entity-effects]}]
                                          {:keys [effect/source]}
                                          world]
                                       (let [{:keys [world/active-entities]} world
                                             source* @source]
                                         (apply concat
                                                (for [target (target-all/affected-targets active-entities world source*)]
                                                  [[:tx/spawn-line
                                                    {:start (:body/position (:entity/body source*)) #_(start-point source* target*)
                                                     :end (:body/position (:entity/body @target))
                                                     :duration 0.05
                                                     :color [1 0 0 0.75]
                                                     :thick? true}]
                                                   [:tx/effect
                                                    {:effect/source source
                                                     :effect/target target}
                                                    entity-effects]]))))}

   :effects/target-entity {:applicable? (fn [[_ {:keys [entity-effects]}] {:keys [effect/target] :as effect-ctx}]
                                          (and target
                                               (seq (filter #(applicable? % effect-ctx) entity-effects))))
                           :useful?     (fn [[_ {:keys [maxrange]}] {:keys [effect/source effect/target]} _world]
                                          (target-entity/in-range? (:entity/body @source)
                                                                   (:entity/body @target)
                                                                   maxrange))
                           :handle      (fn [[_ {:keys [maxrange entity-effects]}]
                                             {:keys [effect/source effect/target] :as effect-ctx}
                                             _world]
                                          (let [body        (:entity/body @source)
                                                target-body (:entity/body @target)]
                                            (if (target-entity/in-range? body target-body maxrange)
                                              [[:tx/spawn-line {:start (target-entity/start-point body target-body)
                                                                :end (:body/position target-body)
                                                                :duration 0.05
                                                                :color [1 0 0 0.75]
                                                                :thick? true}]
                                               [:tx/effect effect-ctx entity-effects]]
                                              [[:tx/audiovisual
                                                (target-entity/end-point body target-body maxrange)
                                                :audiovisuals/hit-ground]])))}

   :effects.target/audiovisual {:applicable? (fn [_ {:keys [effect/target]}]
                                               target)
                                :useful?     (fn [_ _effect-ctx _world]
                                               false)
                                :handle      (fn [[_ audiovisual] {:keys [effect/target]} _world]
                                               [[:tx/audiovisual (:body/position (:entity/body @target)) audiovisual]])}

   :effects.target/convert {:applicable? (fn [_ {:keys [effect/source effect/target]}]
                                           (and target
                                                (= (:entity/faction @target)
                                                   (faction/enemy (:entity/faction @source)))))
                            :handle      (fn [_ {:keys [effect/source effect/target]} _world]
                                           [[:tx/assoc target :entity/faction (:entity/faction @source)]])}

   :effects.target/damage {:applicable? (fn [_ {:keys [effect/target]}]
                                          (and target
                                               #_(:stats/hp @target))) ; not exist anymore ... bugfix .... -> is 'creature?'
                           :handle      (fn [[_ damage]
                                             {:keys [effect/source effect/target]}
                                             _world]
                                          (let [source* @source
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
                                                  (< (rand) (effective-armor-save (:entity/stats source*)
                                                                                  (:entity/stats target*))))
                                             [[:tx/add-text-effect target "[WHITE]ARMOR" 0.3]]

                                             :else
                                             (let [min-max (:damage/min-max (calc-damage (:entity/stats source*)
                                                                                         (:entity/stats target*)
                                                                                         damage))
                                                   dmg-amount (rand-int-between min-max)
                                                   new-hp-val (max (- (hp 0) dmg-amount)
                                                                   0)]
                                               [[:tx/assoc-in target [:entity/stats :stats/hp 0] new-hp-val]
                                                [:tx/event    target (if (zero? new-hp-val) :kill :alert)]
                                                [:tx/audiovisual (:body/position (:entity/body target*)) :audiovisuals/damage]
                                                [:tx/add-text-effect target (str "[RED]" dmg-amount "[]") 0.3]]))))}

   :effects.target/kill {:applicable? (fn [_ {:keys [effect/target]}]
                                        (and target
                                             (:entity/fsm @target)))
                         :handle      (fn [_ {:keys [effect/target]} _world]
                                        [[:tx/event target :kill]])}

   :effects.target/melee-damage {:applicable? (fn [_ {:keys [effect/source] :as effect-ctx}]
                                                (applicable? (melee-damage-effect @source) effect-ctx))
                                 :handle      (fn [_ {:keys [effect/source] :as effect-ctx} world]
                                                (handle (melee-damage-effect @source) effect-ctx world))}

   :effects.target/spiderweb {:applicable? (fn [_ {:keys [effect/target]}]
                                             ; TODO has stats , for mod-add
                                             ; e,g, spiderweb on projectile leads to error
                                             (:entity/stats @target))
                              :handle      (fn [_
                                                {:keys [effect/target]}
                                                world]
                                             ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
                                             (let [{:keys [world/elapsed-time]} world]
                                               (when-not (:entity/temp-modifier @target)
                                                 [[:tx/assoc target :entity/temp-modifier {:modifiers spiderweb-modifiers
                                                                                           :counter (timer/create elapsed-time spiderweb-duration)}]
                                                  [:tx/update target :entity/stats stats/add spiderweb-modifiers]])))}

   :effects.target/stun {:applicable? (fn [_ {:keys [effect/target]}]
                                        (and target
                                             (:entity/fsm @target)))
                         :handle      (fn [[_ duration] {:keys [effect/target]} _world]
                                        [[:tx/event target :stun duration]])}})

(defn applicable? [{k 0 :as component} effect-ctx]
  ((:applicable? (k->fn k)) component effect-ctx))

(defn handle [{k 0 :as component} effect-ctx world]
  ((:handle (k->fn k)) component effect-ctx world))

(defn useful? [{k 0 :as component} effect-ctx world]
  (if-let [f (:useful? (k->fn k))]
    (f component effect-ctx world)
    true))

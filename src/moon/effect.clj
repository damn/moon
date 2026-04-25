(ns moon.effect
  (:require [moon.faction :as faction]
            [moon.rand :refer [rand-int-between]]
            [moon.stats :as stats]
            [moon.timer :as timer]))

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
           stats/apply-max
           (:stats/modifiers target)
           :modifier/damage-receive-max))
  ([source damage]
   (update damage
           :damage/min-max
           #(-> %
                (stats/apply-min (:stats/modifiers source) :modifier/damage-deal-min)
                (stats/apply-max (:stats/modifiers source) :modifier/damage-deal-max)))))

(defmulti applicable?
  (fn [[k _v] _effect-ctx]
    k))

(defmulti handle
  (fn [[k _v] _effect-ctx _ctx]
    k))

(defmulti useful?
  (fn [[k _v] _effect-ctx _ctx]
    k))

(defmethod useful? :default
  [_ _effect-ctx _ctx]
  true)

(defmulti render
  (fn [[k _v] _effect-ctx _ctx]
    k))

(defmethod render :default
  [_ _effect-ctx _ctx]
  nil)

(defmethod applicable? :effects.target/audiovisual
  [_ {:keys [effect/target]}]
  target)

(defmethod useful? :effects.target/audiovisual
  [_ _effect-ctx _ctx]
  false)

(defmethod handle :effects.target/audiovisual
  [[_ audiovisual] {:keys [effect/target]} _ctx]
  [[:tx/audiovisual (:body/position (:entity/body @target)) audiovisual]])

(defmethod applicable? :effects.target/convert
  [_ {:keys [effect/source effect/target]}]
  (and target
       (= (:entity/faction @target)
          (faction/enemy (:entity/faction @source)))))

(defmethod handle :effects.target/convert
  [_ {:keys [effect/source effect/target]} _ctx]
  [[:tx/assoc target :entity/faction (:entity/faction @source)]])

(defmethod applicable? :effects.target/damage
  [_ {:keys [effect/target]}]
  (and target
       #_(:stats/hp @target))) ; not exist anymore ... bugfix .... -> is 'creature?'

(defmethod handle :effects.target/damage
  [[_ damage] {:keys [effect/source effect/target]} _ctx]
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
        [:tx/add-text-effect target (str "[RED]" dmg-amount "[]") 0.3]]))))

(defmethod applicable? :effects.target/kill
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

(defmethod handle :effects.target/kill
  [_ {:keys [effect/target]} _ctx]
  [[:tx/event target :kill]])

(defmethod applicable? :effects.target/melee-damage
  [_ {:keys [effect/source] :as effect-ctx}]
  (applicable? (melee-damage-effect @source) effect-ctx))

(defmethod handle :effects.target/melee-damage
  [_ {:keys [effect/source] :as effect-ctx} ctx]
  (handle (melee-damage-effect @source) effect-ctx ctx))

(def ^:private spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def ^:private spiderweb-duration 5)

(defmethod applicable? :effects.target/spiderweb
  [_ {:keys [effect/target]}]
  ; TODO has stats , for mod-add
  ; e,g, spiderweb on projectile leads to error
  (:entity/stats @target))

(defmethod handle :effects.target/spiderweb
  [_ {:keys [effect/target]} {:keys [ctx/elapsed-time]}]
  ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
  (when-not (:entity/temp-modifier @target)
    [[:tx/assoc target :entity/temp-modifier {:modifiers spiderweb-modifiers
                                              :counter (timer/create elapsed-time spiderweb-duration)}]
     [:tx/update target :entity/stats stats/add spiderweb-modifiers]]))

(defmethod applicable? :effects.target/stun
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

(defmethod handle :effects.target/stun
  [[_ duration] {:keys [effect/target]} _ctx]
  [[:tx/event target :stun duration]])

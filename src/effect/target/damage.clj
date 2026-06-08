(ns effect.target.damage
  (:require [clojure.rand.int-between :refer [rand-int-between]]
            [game.effect :as effect]
            [moon.stats.get-stat-value :refer [get-stat-value]]
            [moon.stats.get-hitpoints :as get-hitpoints]
            [moon.stats.calc-damage :as calc-damage]))

; not in stats because projectile as source doesnt have stats
; FIXME I don't see it triggering with 10 armor save ... !
(defn- effective-armor-save [source-stats target-stats]
  (max (- (or (get-stat-value source-stats :stats/armor-save)   0)
          (or (get-stat-value target-stats :stats/armor-pierce) 0))
       0))

(comment

 (effective-armor-save {} {:stats/modifiers {:modifiers/armor-save {:op/inc 10}}
                           :stats/armor-save 0})
 ; broken
 (let [source* {:stats/armor-pierce 0.4}
       target* {:stats/armor-save   0.5}]
   (effective-armor-save source* target*))
 )

(defmethod effect/applicable? :effects.target/damage
  [_ {:keys [effect/target]}]
  (and target
       #_(:stats/hp @target))) ; not exist anymore ... bugfix .... -> is 'creature?'

(defmethod effect/handle :effects.target/damage
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
          (< (rand) (effective-armor-save (:entity/stats source*)
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

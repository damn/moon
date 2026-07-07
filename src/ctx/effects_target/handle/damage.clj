(ns ctx.effects-target.handle.damage
  (:require [clojure.int-between :refer [rand-int-between]]
            [moon.stats.effective-armor-save :as effective-armor-save]
            [moon.stats.get-hitpoints :as get-hitpoints]
            [moon.stats.calc-damage :as calc-damage]))

(defn f
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

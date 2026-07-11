(ns moon.stats
  (:require [clojure.mods.add :as add-mods-op]
            [clojure.mods.remove :as remove-mods-op]
            [clojure.modifiers-get-value :as get-value]
            [clojure.string :as str]
            [clojure.val-max.apply-max :as apply-max]
            [clojure.val-max.apply-min :as apply-min]))

(def ^:private non-val-max-stat-ks
  [:stats/movement-speed
   :stats/aggro-range
   :stats/reaction-time
   :stats/strength
   :stats/cast-speed
   :stats/attack-speed
   :stats/armor-save
   :stats/armor-pierce])

(defn get-hitpoints
  [{:keys [stats/hp
           stats/modifiers]}]
  (apply-max/f hp modifiers :modifier/hp-max))

(defn get-mana
  [{:keys [stats/mana
           stats/modifiers]}]
  (apply-max/f mana modifiers :modifier/mana-max))

(defn not-enough-mana?
  [stats {:keys [skill/cost]}]
  (> cost ((get-mana stats) 0)))

(defn pay-mana-cost [stats cost]
  (let [mana-val ((get-mana stats) 0)]
    (assert (<= cost mana-val))
    (assoc-in stats [:stats/mana 0] (- mana-val cost))))

(defn get-value
  [stats stat-k]
  (when-let [base-value (stat-k stats)]
    (get-value/f base-value
                 (:stats/modifiers stats)
                 (keyword "modifier" (name stat-k)))))

(defn add-mods [stats mods]
  (update stats :stats/modifiers add-mods-op/f mods))

(defn remove-mods [stats mods]
  (update stats :stats/modifiers remove-mods-op/f mods))

(defn apply-action-speed-modifier [stats skill action-time]
  (/ action-time
     (or (get-value stats (:skill/action-time-modifier-key skill))
         1)))

(defn calc-damage
  ([source target damage]
   (update (calc-damage source damage)
           :damage/min-max
           apply-max/f
           (:stats/modifiers target)
           :modifier/damage-receive-max))
  ([source damage]
   (update damage
           :damage/min-max
           #(-> %
                (apply-min/f (:stats/modifiers source) :modifier/damage-deal-min)
                (apply-max/f (:stats/modifiers source) :modifier/damage-deal-max)))))

(defn effective-armor-save [source-stats target-stats]
  (max (- (or (get-value source-stats :stats/armor-save) 0)
          (or (get-value target-stats :stats/armor-pierce) 0))
       0))

(defn melee-damage [{:keys [entity/stats]}]
  (let [strength (or (get-value stats :stats/strength) 0)]
    {:damage/min-max [strength strength]}))

(defn format-text
  [stats]
  (str/join "\n" (concat
                   ["*STATS*"
                    (str "Mana: " (get-mana stats))
                    (str "Hitpoints: " (get-hitpoints stats))]
                   (for [stat-k non-val-max-stat-ks]
                     (str (str/capitalize (name stat-k)) ": "
                          (get-value stats stat-k))))))

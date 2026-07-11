(ns moon.stats
  (:require [clojure.modifiers-get-value :as get-value]
            [clojure.string :as str]
            [clojure.val-max.apply-max :as apply-max]))

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

(defn get-value
  [stats stat-k]
  (when-let [base-value (stat-k stats)]
    (get-value/f base-value
                 (:stats/modifiers stats)
                 (keyword "modifier" (name stat-k)))))

(defn format-text
  [stats]
  (str/join "\n" (concat
                   ["*STATS*"
                    (str "Mana: " (get-mana stats))
                    (str "Hitpoints: " (get-hitpoints stats))]
                   (for [stat-k non-val-max-stat-ks]
                     (str (str/capitalize (name stat-k)) ": "
                          (get-value stats stat-k))))))

(ns clojure.entity-stats
  (:require [clojure.string :as str]
            [clojure.get-stat-value :refer [get-stat-value]]
            [clojure.get-mana :as get-mana]
            [clojure.get-hitpoints :as get-hitpoints]))

(let [non-val-max-stat-ks
      [:stats/movement-speed
       :stats/aggro-range
       :stats/reaction-time
       :stats/strength
       :stats/cast-speed
       :stats/attack-speed
       :stats/armor-save
       :stats/armor-pierce]]
  (defn f [stats _ctx]
    (str/join "\n" (concat
                    ["*STATS*"
                     (str "Mana: " (get-mana/f stats))
                     (str "Hitpoints: " (get-hitpoints/f stats))]
                    (for [stat-k non-val-max-stat-ks]
                      (str (str/capitalize (name stat-k)) ": "
                           (get-stat-value stats stat-k)))))))

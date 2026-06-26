(ns info.entity.stats
  (:require [clojure.string :as str]
            [moon.stats.get-stat-value :refer [get-stat-value]]
            [moon.stats.get-mana :as get-mana]
            [moon.stats.get-hitpoints :as get-hitpoints]))

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

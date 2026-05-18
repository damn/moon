(ns moon.stats)

(defprotocol Stats
  (get-stat-value [_ stat-k])
  (add [_ mods])
  (remove-mods [_ mods])
  (get-mana [_])
  (not-enough-mana? [_ skill])
  (pay-mana-cost [stats cost])
  (get-hitpoints [_])
  (calc-damage [source target damage]
               [source damage]))


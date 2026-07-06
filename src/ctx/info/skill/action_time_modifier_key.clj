(ns ctx.info.skill.action-time-modifier-key)

(defn f [v _ctx]
  (case v
    :stats/cast-speed "Spell"
    :stats/attack-speed "Attack"))

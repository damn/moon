(ns ctx.effects-target.applicable.damage)

(defn f
  [_ {:keys [effect/target]}]
  (and target
       #_(:stats/hp @target))) ; not exist anymore ... bugfix .... -> is 'creature?'

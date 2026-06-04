(ns info.effects.target.damage)

(defn f [{[min max] :damage/min-max} _ctx]
  (str min "-" max " damage")
  #_(if source
      (let [modified (stats/damage @source damage)]
        (if (= damage modified)
          (damage/info-text damage)
          (str (damage/info-text damage) "\nModified: " (damage/info modified))))
      (damage/info-text damage)) )

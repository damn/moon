(ns effects.applicable.projectile)

(defn f
  [_ {:keys [effect/target-direction]}]
  ; TODO for npcs need target -- anyway only with direction
  ; faction @ source also ?
  target-direction)

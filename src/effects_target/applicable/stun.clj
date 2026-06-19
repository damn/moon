(ns effects-target.applicable.stun)

(defn f
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

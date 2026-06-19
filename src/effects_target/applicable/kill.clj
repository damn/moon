(ns effects-target.applicable.kill)

(defn f
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

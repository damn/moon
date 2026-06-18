(ns effect.applicable.spawn)

(defn f
  [_ {:keys [effect/source effect/target-position]}]
  (and (:entity/faction @source)
       target-position))

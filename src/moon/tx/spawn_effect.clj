(ns moon.tx.spawn-effect)

(defn do!
  [{:keys [ctx/world]}
   position
   components]
  [[:tx/spawn-entity
    (assoc components
           :entity/body (assoc (:world/effect-body-props world) :position position))]])

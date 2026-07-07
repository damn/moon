(ns clojure.spawn-effect)

(defn do! [_ctx position components]
  [[:tx/spawn-entity
    (assoc components
           :entity/body {:width 0.5
                         :height 0.5
                         :z-order :z-order/effect
                         :position position})]])

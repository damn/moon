(ns effects.handle.target-all
  (:require [effect.target-all]))

(defn f
  [[_ {:keys [entity-effects]}]
   {:keys [effect/source]}
   {:keys [ctx/active-entities
           ctx/colors
           ctx/raycaster]}]
  (let [source* @source]
    (apply concat
           (for [target (effect.target-all/affected-targets active-entities raycaster source*)]
             [[:tx/spawn-line
               {:start (:body/position (:entity/body source*)) #_(start-point source* target*)
                :end (:body/position (:entity/body @target))
                :duration 0.05
                :color (:colors/target-all-line colors)
                :thick? true}]
              [:tx/effect
               {:effect/source source
                :effect/target target}
               entity-effects]]))))

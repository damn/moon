(ns game.effect.target-all
  (:require [moon.effect :as effect]
            [moon.raycaster :as raycaster]))

(defn- affected-targets [active-entities raycaster entity]
  (->> active-entities
       (filter #(:entity/species @%))
       (filter #(raycaster/line-of-sight? raycaster entity @%))
       (remove #(:entity/player? @%))))

(defmethod effect/applicable? :effects/target-all
  [_ _] ; TODO check ..
  true)

(defmethod effect/useful? :effects/target-all
  [_ _effect-ctx _ctx]
  false)

(defmethod effect/handle :effects/target-all
  [[_ {:keys [entity-effects]}]
   {:keys [effect/source]}
   {:keys [ctx/active-entities
           ctx/colors
           ctx/raycaster]}]
  (let [source* @source]
    (apply concat
           (for [target (affected-targets active-entities raycaster source*)]
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

(defmethod effect/render :effects/target-all
  [_
   {:keys [effect/source]}
   {:keys [ctx/active-entities
           ctx/colors
           ctx/raycaster]}]
  (let [source* @source]
    (for [target* (map deref (affected-targets active-entities raycaster source*))]
      [:draw/line
       (:body/position (:entity/body source*)) #_(start-point source* target*)
       (:body/position (:entity/body target*))

       (:colors/target-all-render colors)])))

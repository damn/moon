(ns moon.effects.target-all
  (:require [moon.effect :as effect]
            [moon.raycaster :as raycaster]))

(defn affected-targets [active-entities raycaster entity]
  (->> active-entities
       (filter #(:entity/species @%))
       (filter #(raycaster/line-of-sight? raycaster entity @%))
       (remove #(:entity/player? @%))))

(comment
 ; TODO applicable targets? e.g. projectiles/effect s/???item entiteis ??? check
 ; same code as in render entities on world view screens/world
 ; TODO showing one a bit further up
 ; maybe world view port is cut
 ; not quite showing correctly.
 (let [targets (creatures-in-los-of-player)]
   (count targets)
   #_(sort-by #(% 1) (map #(vector (:entity.creature/name @%)
                                   (:body/position (:entity/body @%))) targets)))

 )

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
           ctx/raycaster]}]
  (let [source* @source]
    (apply concat
           (for [target (affected-targets active-entities raycaster source*)]
             [[:tx/spawn-line
               {:start (:body/position (:entity/body source*)) #_(start-point source* target*)
                :end (:body/position (:entity/body @target))
                :duration 0.05
                :color [1 0 0 0.75]
                :thick? true}]
              [:tx/effect
               {:effect/source source
                :effect/target target}
               entity-effects]]))))

(defmethod effect/render :effects/target-all
  [_
   {:keys [effect/source]}
   {:keys [ctx/active-entities
           ctx/raycaster]}]
  (let [source* @source]
    (for [target* (map deref (affected-targets active-entities raycaster source*))]
      [:draw/line
       (:body/position (:entity/body source*)) #_(start-point source* target*)
       (:body/position (:entity/body target*))
       [1 0 0 0.5]])))

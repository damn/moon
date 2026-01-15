(ns moon.effects.target.convert
  (:require [moon.effect :as effect]
            [moon.faction :as faction]))

(defmethod effect/applicable? :effects.target/convert
  [_ {:keys [effect/source effect/target]}]
  (and target
       (= (:entity/faction @target)
          (faction/enemy (:entity/faction @source)))))

(defmethod effect/handle :effects.target/convert
  [_ {:keys [effect/source effect/target]} _ctx]
  [[:tx/assoc target :entity/faction (:entity/faction @source)]])

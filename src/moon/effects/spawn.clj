(ns moon.effects.spawn
  (:require [moon.effect :as effect]))

(defmethod effect/applicable? :effects/spawn
  [_ {:keys [effect/source effect/target-position]}]
  (and (:entity/faction @source)
       target-position))

(defmethod effect/handle :effects/spawn
  [[_ {:keys [property/id] :as property}]
   {:keys [effect/source effect/target-position]}
   _world]
  [[:tx/spawn-creature {:position target-position
                        :creature-property property
                        :components {:entity/fsm {:fsm :fsms/npc
                                                  :initial-state :npc-idle}
                                     :entity/faction (:entity/faction @source)}}]])

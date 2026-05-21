(ns game.state.npc-idle
  (:require [moon.body :as body]
            [moon.effect :as effect]
            [moon.entity :as entity]
            [moon.grid :as grid]
            [moon.raycaster :as raycaster]
            [moon.skill :as skill]))

(defn- npc-effect-ctx
  [{:keys [ctx/grid
           ctx/raycaster]}
   eid]
  (let [entity @eid
        target (grid/nearest-enemy grid entity)
        target (when (and target
                          (raycaster/line-of-sight? raycaster entity @target))
                 target)]
    {:effect/source eid
     :effect/target target
     :effect/target-direction (when target
                                (body/direction (:entity/body entity)
                                                (:entity/body @target)))}))

(defn- npc-choose-skill [ctx entity effect-ctx]
  (->> entity
       :entity/skills
       vals
       (sort-by :skill/cost)
       reverse
       (filter #(and (= :usable (skill/usable-state % entity effect-ctx))
                     (->> (:skill/effects %)
                          (filter (fn [e] (effect/applicable? e effect-ctx)))
                          (some (fn [e] (effect/useful? e effect-ctx ctx))))))
       first))

(defmethod entity/tick :npc-idle
  [_ eid ctx]
  (let [effect-ctx (npc-effect-ctx ctx eid)]
    (if-let [skill (npc-choose-skill ctx @eid effect-ctx)]
      [[:tx/event eid :start-action [skill effect-ctx]]]
      [[:tx/event eid :movement-direction (or (grid/find-direction (:ctx/grid ctx) eid)
                                              [0 0])]])))

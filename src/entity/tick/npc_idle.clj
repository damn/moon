(ns entity.tick.npc-idle
  (:require [entity.tick.npc-idle.choose-skill :as choose-skill]
            [entity.tick.npc-idle.create-effect-ctx :as create-effect-ctx]
            [moon.grid.npc-pathing :as npc-pathing]))

(defn f
  [_ eid ctx]
  (let [effect-ctx (create-effect-ctx/f ctx eid)]
    (if-let [skill (choose-skill/f ctx @eid effect-ctx)]
      [[:tx/event eid :start-action [skill effect-ctx]]]
      [[:tx/event eid :movement-direction (or (npc-pathing/find-direction (:ctx/grid ctx) eid)
                                              [0 0])]])))

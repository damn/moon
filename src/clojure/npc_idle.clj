(ns clojure.npc-idle
  (:require [clojure.choose-skill :as choose-skill]
            [clojure.create-effect-ctx :as create-effect-ctx]
            [clojure.npc-pathing :as npc-pathing]))

(defn f
  [_ eid ctx]
  (let [effect-ctx (create-effect-ctx/f ctx eid)]
    (if-let [skill (choose-skill/f ctx @eid effect-ctx)]
      [[:tx/event eid :start-action [skill effect-ctx]]]
      [[:tx/event eid :movement-direction (or (npc-pathing/find-direction (:ctx/grid ctx) eid)
                                              [0 0])]])))

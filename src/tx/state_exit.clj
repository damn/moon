(ns tx.state-exit
  (:require entity.state.exit.player-item-on-cursor
            entity.state.exit.player-moving
            entity.state.exit.npc-sleeping
            entity.state.exit.npc-moving))

(def k->f
  {
   :player-item-on-cursor entity.state.exit.player-item-on-cursor/f
   :player-moving entity.state.exit.player-moving/f
   :npc-sleeping entity.state.exit.npc-sleeping/f
   :npc-moving entity.state.exit.npc-moving/f
   }
  )

(defn do! [ctx eid [state-k state-v]]
  (if-let [f (k->f state-k)]
    (f state-v eid ctx)
    nil))

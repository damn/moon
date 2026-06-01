(ns tx.state-enter
  (:require entity.state.enter.player-item-on-cursor
            entity.state.enter.active-skill
            entity.state.enter.npc-dead
            entity.state.enter.player-moving
            entity.state.enter.player-dead
            entity.state.enter.npc-moving))

(def k->fn
  {
   :player-item-on-cursor entity.state.enter.player-item-on-cursor/f
   :active-skill entity.state.enter.active-skill/f
   :npc-dead entity.state.enter.npc-dead/f
   :player-moving entity.state.enter.player-moving/f
   :player-dead entity.state.enter.player-dead/f
   :npc-moving entity.state.enter.npc-moving/f
   })

(defn do! [_ctx eid [state-k state-v]]
  (if-let [f (k->fn state-k)]
    (f state-v eid)
    nil))

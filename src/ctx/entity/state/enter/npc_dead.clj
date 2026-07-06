(ns ctx.entity.state.enter.npc-dead)

(defn f
  [_ eid]
  [[:tx/mark-destroyed eid]])

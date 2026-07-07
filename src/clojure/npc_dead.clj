(ns clojure.npc-dead)

(defn f
  [_ eid]
  [[:tx/mark-destroyed eid]])

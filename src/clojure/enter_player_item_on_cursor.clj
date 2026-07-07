(ns clojure.enter-player-item-on-cursor)

(defn f
  [{:keys [item]} eid]
  [[:tx/assoc eid :entity/item-on-cursor item]])

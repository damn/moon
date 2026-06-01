(ns entity.state.load
  (:require [game.state :as state]))

(defmethod state/clicked-inventory-cell :default
  [_ _eid _cell]
  nil)

(defmethod state/clicked-inventory-cell :player-idle
  [_ eid cell]
  (when-let [item (get-in (:entity/inventory @eid) cell)]
    [[:tx/sound "bfxr_takeit"]
     [:tx/event eid :pickup-item item]
     [:tx/remove-item eid cell]]))

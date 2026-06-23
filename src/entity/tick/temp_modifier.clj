(ns entity.tick.temp-modifier
  (:require [timer.stopped :refer [stopped?]]
            [moon.stats.remove-mods :as remove-mods]))

(defn f
  [{:keys [modifiers counter]}
   eid
   {:keys [ctx/elapsed-time]}]
  (when (stopped? elapsed-time counter)
    [[:tx/dissoc eid :entity/temp-modifier]
     [:tx/update eid :entity/stats remove-mods/f modifiers]]))

(ns entity.tick.temp-modifier
  (:require [moon.timer :as timer]
            [moon.stats :as stats]))

(defn f
  [{:keys [modifiers counter]}
   eid
   {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/dissoc eid :entity/temp-modifier]
     [:tx/update eid :entity/stats stats/remove-mods modifiers]]))

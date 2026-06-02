(ns entity.tick.temp-modifier
  (:require [clojure.timer.stopped :refer [stopped?]]
            [moon.stats :as stats]))

(defn f
  [{:keys [modifiers counter]}
   eid
   {:keys [ctx/elapsed-time]}]
  (when (stopped? elapsed-time counter)
    [[:tx/dissoc eid :entity/temp-modifier]
     [:tx/update eid :entity/stats stats/remove-mods modifiers]]))

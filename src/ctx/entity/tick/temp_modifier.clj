(ns ctx.entity.tick.temp-modifier
  (:require [clojure.stopped :refer [stopped?]]
            [moon.stats.remove-mods :as remove-mods]))

(defn f
  [{:keys [modifiers counter]}
   eid
   {:keys [ctx/elapsed-time]}]
  (when (stopped? elapsed-time counter)
    [[:tx/dissoc eid :entity/temp-modifier]
     [:tx/update eid :entity/stats remove-mods/f modifiers]]))

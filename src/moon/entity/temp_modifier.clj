(ns moon.entity.temp-modifier
  (:require [moon.entity.stats :as stats]
            [moon.timer :as timer]))

(defn tick
  [[_k {:keys [modifiers counter]}]
   eid
   {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/dissoc eid :entity/temp-modifier]
     [:tx/update eid :entity/stats stats/remove-mods modifiers]]))

(defn render
  [_ entity _ctx]
  [[:draw/filled-circle
    (:body/position (:entity/body entity))
    0.5
    [0.5 0.5 0.5 0.4]]])

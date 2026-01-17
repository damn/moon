(ns moon.entity.temp-modifier
  (:require [moon.entity :as entity]
            [moon.entity.stats :as stats]
            [moon.timer :as timer]))

(defmethod entity/tick :entity/temp-modifier
  [[_k {:keys [modifiers counter]}]
   eid
   {:keys [ctx/world]}]
  (when (timer/stopped? (:world/elapsed-time world) counter)
    [[:tx/dissoc eid :entity/temp-modifier]
     [:tx/update eid :entity/stats stats/remove-mods modifiers]]))

(defmethod entity/render :entity/temp-modifier
  [_ entity _ctx]
  [[:draw/filled-circle
    (:body/position (:entity/body entity))
    0.5
    [0.5 0.5 0.5 0.4]]])

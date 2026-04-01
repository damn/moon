(ns moon.reaction-txs.toggle-inventory-visible
  (:require [moon.actor :as actor]
            [moon.stage :as stage]))

(defn do!
  [{:keys [ctx/stage] :as ctx}]
  (-> stage
      (stage/find-actor "moon.ui.windows.inventory")
      actor/toggle-visible!)
  ctx)

(ns moon.reaction-txs.toggle-inventory-visible
  (:require [moon.stage :as stage]
            [moon.ui.actor :as actor]))

(defn do!
  [{:keys [ctx/stage] :as ctx}]
  (-> stage
      (stage/find-actor "moon.ui.windows.inventory")
      actor/toggle-visible!)
  ctx)

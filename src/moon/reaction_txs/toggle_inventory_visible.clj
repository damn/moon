(ns moon.reaction-txs.toggle-inventory-visible
  (:require [moon.stage :as stage]
            [moon.ui.actor :as actor]
            [moon.ui.group :as group]))

(defn do!
  [{:keys [ctx/stage] :as ctx}]
  (-> stage
      stage/root
      (group/find-actor "moon.ui.windows")
      (group/find-actor "moon.ui.windows.inventory")
      actor/toggle-visible!)
  ctx)

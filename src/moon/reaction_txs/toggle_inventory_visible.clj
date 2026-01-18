(ns moon.reaction-txs.toggle-inventory-visible
  (:require [moon.ui.actor :as actor]))

(defn do!
  [{:keys [ctx/stage] :as ctx}]
  (-> stage
      .getRoot
      (.findActor "moon.ui.windows")
      (.findActor "moon.ui.windows.inventory")
      actor/toggle-visible!)
  ctx)

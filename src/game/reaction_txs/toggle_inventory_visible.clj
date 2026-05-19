(ns game.reaction-txs.toggle-inventory-visible
  (:require [gdl.scene2d.stage :as stage]
            [gdl.scene2d.actor :as actor]))

(defn do!
  [{:keys [ctx/stage] :as ctx}]
  (-> stage
      (stage/find-actor "moon.ui.windows.inventory")
      actor/toggle-visible!)
  ctx)

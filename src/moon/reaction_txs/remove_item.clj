(ns moon.reaction-txs.remove-item
  (:require [moon.stage :as stage]
            [moon.ui.inventory :as inventory-window]
            [moon.ui.group :as group]))

(defn do!
  [{:keys [ctx/stage] :as ctx} eid cell]
  (when (:entity/player? @eid)
    (-> stage
        stage/root
        (group/find-actor "moon.ui.windows")
        (group/find-actor "moon.ui.windows.inventory")
        (inventory-window/remove-item! cell)))
  ctx)

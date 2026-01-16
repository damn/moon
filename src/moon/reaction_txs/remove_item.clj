(ns moon.reaction-txs.remove-item
  (:require [moon.ui.inventory :as inventory-window]))

(defn do!
  [{:keys [ctx/stage] :as ctx} eid cell]
  (when (:entity/player? @eid)
    (-> stage
        .getRoot
        (.findActor "moon.ui.windows")
        (.findActor "moon.ui.windows.inventory")
        (inventory-window/remove-item! cell)))
  ctx)

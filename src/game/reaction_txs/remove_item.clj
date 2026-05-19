(ns game.reaction-txs.remove-item
  (:require [gdl.scene2d.stage :as stage]
            [moon.ui.inventory-window :as inventory-window]))

(defn do!
  [{:keys [ctx/stage] :as ctx} eid cell]
  (when (:entity/player? @eid)
    (-> stage
        ;(group/find-actor "moon.ui.windows")
        (stage/find-actor "moon.ui.windows.inventory")
        (inventory-window/remove-item! cell)))
  ctx)

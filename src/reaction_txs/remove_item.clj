(ns reaction-txs.remove-item
  (:require [gdx.stage :as stage]
            [moon.ui.inventory-window :as inventory-window]))

(defn f
  [{:keys [ctx/stage] :as ctx} eid cell]
  (when (:entity/player? @eid)
    (-> stage
        (stage/find-actor "moon.ui.windows.inventory")
        (inventory-window/remove-item! cell)))
  ctx)

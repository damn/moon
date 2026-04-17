(ns moon.reaction-txs.remove-item
  (:require [moon.inventory-window :as inventory-window]
            [clojure.scene2d.stage :as stage]))

(defn do!
  [{:keys [ctx/stage] :as ctx} eid cell]
  (when (:entity/player? @eid)
    (-> stage
        ;(group/find-actor "moon.ui.windows")
        (stage/find-actor "moon.ui.windows.inventory")
        (inventory-window/remove-item! cell)))
  ctx)

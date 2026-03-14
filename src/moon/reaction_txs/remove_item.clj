(ns moon.reaction-txs.remove-item
  (:require [moon.ui.inventory-window :as inventory-window])
  (:import (com.badlogic.gdx.scenes.scene2d Group
                                            Stage)))

(defn do!
  [{:keys [ctx/stage] :as ctx} eid cell]
  (when (:entity/player? @eid)
    (-> stage
        Stage/.getRoot
        (Group/.findActor "moon.ui.windows")
        (Group/.findActor "moon.ui.windows.inventory")
        (inventory-window/remove-item! cell)))
  ctx)

(ns reaction-txs.remove-item
  (:require [clojure.scene2d.group.find-actor :refer [find-actor]]
            [moon.ui.inventory-window :as inventory-window]))

(defn f
  [{:keys [ctx/stage] :as ctx} eid cell]
  (when (:entity/player? @eid)
    (-> stage
        :stage/root
        (find-actor "moon.ui.windows.inventory")
        (inventory-window/remove-item! cell)))
  ctx)

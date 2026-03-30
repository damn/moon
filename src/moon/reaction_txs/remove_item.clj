(ns moon.reaction-txs.remove-item
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.group :as group]
            [moon.inventory-window :as inventory-window])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn do!
  [{:keys [ctx/stage] :as ctx} eid cell]
  (when (:entity/player? @eid)
    (-> stage
        Stage/.getRoot
        (group/find-actor "moon.ui.windows")
        (group/find-actor "moon.ui.windows.inventory")
        (inventory-window/remove-item! cell)))
  ctx)

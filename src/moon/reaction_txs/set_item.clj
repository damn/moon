(ns moon.reaction-txs.set-item
  (:require [moon.info :as info]
            [moon.textures :as textures]
            [moon.ui.inventory :as inventory-window])
  (:import (com.badlogic.gdx.scenes.scene2d Group
                                            Stage)))

(defn do!
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid cell item]
  (when (:entity/player? @eid)
    (-> stage
        Stage/.getRoot
        (Group/.findActor "moon.ui.windows")
        (Group/.findActor "moon.ui.windows.inventory")
        (inventory-window/set-item! cell {:texture-region (textures/texture-region textures (:entity/image item))
                                          :tooltip-text (info/text item ctx)}
                                    skin)))
  ctx)

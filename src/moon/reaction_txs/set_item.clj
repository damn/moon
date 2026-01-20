(ns moon.reaction-txs.set-item
  (:require [moon.info :as info]
            [moon.textures :as textures]
            [moon.stage :as stage]
            [moon.ui.inventory :as inventory-window]
            [moon.ui.group :as group]))

(defn do!
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid cell item]
  (when (:entity/player? @eid)
    (-> stage
        stage/root
        (group/find-actor "moon.ui.windows")
        (group/find-actor "moon.ui.windows.inventory")
        (inventory-window/set-item! cell {:texture-region (textures/texture-region textures (:entity/image item))
                                          :tooltip-text (info/text item ctx)}
                                    skin)))
  ctx)

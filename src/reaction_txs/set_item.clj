(ns reaction-txs.set-item
  (:require [gdx.stage :as stage]
            [moon.ui.inventory-window :as inventory-window]
            [moon.textures :as textures]
            [game.info :as info]))

(defn f
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid cell item]
  (when (:entity/player? @eid)
    (-> stage
        ;(group/find-actor "moon.ui.windows")
        (stage/find-actor "moon.ui.windows.inventory")
        (inventory-window/set-item! cell {:texture-region (textures/texture-region textures (:entity/image item))
                                          :tooltip-text (info/text item ctx)}
                                    skin)))
  ctx)

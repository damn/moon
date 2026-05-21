(ns game.reaction-txs.set-item
  (:require [moon.info :as info]
            [clojure.scene2d.stage :as stage]
            [moon.textures :as textures]
            [moon.ui.inventory-window :as inventory-window]))

(defn do!
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

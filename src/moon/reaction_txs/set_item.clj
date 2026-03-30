(ns moon.reaction-txs.set-item
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.group :as group]
            [moon.info :as info]
            [moon.inventory-window :as inventory-window]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn do!
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid cell item]
  (when (:entity/player? @eid)
    (-> stage
        Stage/.getRoot
        (group/find-actor "moon.ui.windows")
        (group/find-actor "moon.ui.windows.inventory")
        (inventory-window/set-item! cell {:texture-region (textures/texture-region textures (:entity/image item))
                                          :tooltip-text (info/text item ctx)}
                                    skin)))
  ctx)

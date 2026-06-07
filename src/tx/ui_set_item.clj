(ns tx.ui-set-item
  (:require [com.badlogic.gdx.scenes.scene2d.group.find-actor :refer [find-actor]]
            [moon.ui.inventory-window :as inventory-window]
            [moon.textures :as textures]
            [game.info :as info]))

(defn f
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid cell item]
  (-> stage
      :stage/root
      (find-actor "moon.ui.windows.inventory")
      (inventory-window/set-item! cell {:texture-region (textures/texture-region textures (:entity/image item))
                                        :tooltip-text (info/text item ctx)}
                                  skin))
  nil)

(ns tx.ui-set-item
  (:require
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [moon.inventory-window.set-item :as set-item]
            [moon.textures :as textures]
            [info.item :refer [info-text]]))

(defn f
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid cell item]
  (-> stage
      :stage/root
      (#(group/find-actor % "moon.ui.windows.inventory"))
      (set-item/f cell {:texture-region (textures/texture-region textures (:entity/image item))
                        :tooltip-text (info-text item ctx)}
                  skin))
  nil)

(ns tx.ui-set-item
  (:require [scene2d.group.find-actor :refer [find-actor]]
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
      (find-actor "moon.ui.windows.inventory")
      (set-item/f cell {:texture-region (textures/texture-region textures (:entity/image item))
                        :tooltip-text (info-text item ctx)}
                  skin))
  nil)

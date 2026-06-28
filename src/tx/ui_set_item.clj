(ns tx.ui-set-item
  (:require [moon.inventory-window.set-item :as set-item]
            [moon.textures :as textures]
            [info.item :refer [info-text]])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn f
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid cell item]
  (-> stage
      :stage/root
      (#(Group/.findActor % "moon.ui.windows.inventory"))
      (set-item/f cell {:texture-region (textures/texture-region textures (:entity/image item))
                        :tooltip-text (info-text item ctx)}
                  skin))
  nil)

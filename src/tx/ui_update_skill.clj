(ns tx.ui-update-skill
  (:require [moon.action-bar.add-skill :as add-skill]
            [moon.textures :as textures]
            [info.entity :refer [info-text]])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn f
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid skill]
  (-> stage
      :stage/root
      (#(Group/.findActor % "moon.ui.action-bar"))
      (add-skill/f {:skill-id (:property/id skill)
                    :texture-region (textures/texture-region textures (:entity/image skill))
                    :tooltip-text (info-text skill ctx)}
                   skin))
  nil)

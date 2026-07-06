(ns ctx.tx.ui-update-skill
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]
            [moon.action-bar.add-skill :as add-skill]
            [moon.textures :as textures]
            [ctx.info.entity :refer [info-text]]))

(defn f
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid skill]
  (-> stage
      :stage/root
      (#(group/find-actor % "moon.ui.action-bar"))
      (add-skill/f {:skill-id (:property/id skill)
                    :texture-region (textures/texture-region textures (:entity/image skill))
                    :tooltip-text (info-text skill ctx)}
                   skin))
  nil)

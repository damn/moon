(ns tx.ui-update-skill
  (:require [clojure.gdx.group.find-actor :as find-actor]
            [moon.action-bar.add-skill :as add-skill]
            [moon.textures :as textures]
            [info.entity :refer [info-text]]))

(defn f
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid skill]
  (-> stage
      :stage/root
      (#(find-actor/f % "moon.ui.action-bar"))
      (add-skill/f {:skill-id (:property/id skill)
                    :texture-region (textures/texture-region textures (:entity/image skill))
                    :tooltip-text (info-text skill ctx)}
                   skin))
  nil)

(ns reaction-txs.add-skill
  (:require [gdx.stage :as stage]
            [gdx.scenes.scene2d.ui.action-bar :as action-bar]
            [moon.textures :as textures]
            [game.info :as info]))

(defn f
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid skill]
  (when (:entity/player? @eid)
    (-> stage
        (stage/find-actor "moon.ui.action-bar")
        (action-bar/add-skill! {:skill-id (:property/id skill)
                                :texture-region (textures/texture-region textures (:entity/image skill))
                                :tooltip-text (info/text skill ctx)}
                               skin)))
  ctx)

(ns reaction-txs.add-skill
  (:require [clojure.scene2d.group.find-actor :refer [find-actor]]
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
        :stage/root
        (find-actor "moon.ui.action-bar")
        (action-bar/add-skill! {:skill-id (:property/id skill)
                                :texture-region (textures/texture-region textures (:entity/image skill))
                                :tooltip-text (info/text skill ctx)}
                               skin)))
  ctx)

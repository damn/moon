(ns moon.reaction-txs.add-skill
  (:require [moon.info :as info]
            [moon.textures :as textures]
            [moon.ui.action-bar :as action-bar]))

(defn do!
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid skill]
  (when (:entity/player? @eid)
    (-> stage
        .getRoot
        (.findActor "moon.ui.action-bar")
        (action-bar/add-skill! {:skill-id (:property/id skill)
                                :texture-region (textures/texture-region textures (:entity/image skill))
                                :tooltip-text (info/text skill ctx)}
                               skin)))
  ctx)

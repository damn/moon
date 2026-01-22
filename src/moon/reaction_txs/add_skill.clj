(ns moon.reaction-txs.add-skill
  (:require [moon.info :as info]
            [moon.textures :as textures]
            [moon.stage :as stage]
            [moon.ui.action-bar :as action-bar]
            [moon.ui.group :as group]))

(defn do!
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid skill]
  (when (:entity/player? @eid)
    (-> stage
        stage/root
        (group/find-actor "moon.ui.action-bar")
        (action-bar/add-skill! {:skill-id (:property/id skill)
                                :texture-region (textures/texture-region textures (:entity/image skill))
                                :tooltip-text (info/text skill ctx)}
                               skin)))
  ctx)

#_(remove-skill! [stage skill-id]
                 (-> stage
                     .getRoot
                     (.findActor "moon.ui.action-bar")
                     (action-bar/remove-skill! skill-id)))

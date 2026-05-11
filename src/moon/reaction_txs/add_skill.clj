(ns moon.reaction-txs.add-skill
  (:require [moon.info :as info]
            [moon.stage :as stage]
            [moon.textures :as textures]
            [moon.ui-actors.action-bar :as action-bar]))

(defn do!
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

   #_(remove-skill! [stage skill-id]
                    (-> stage
                        (stage/find-actor "moon.ui.action-bar")
                        (action-bar/remove-skill! skill-id)))

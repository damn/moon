(ns moon.reaction-txs.add-skill
  (:require [moon.textures :as textures]
            [moon.ui :as ui]
            [moon.world.info :as info]))

(defn do!
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid skill]
  (when (:entity/player? @eid)
    (ui/add-skill! stage
                   {:skill-id (:property/id skill)
                    :texture-region (textures/texture-region textures (:entity/image skill))
                    :tooltip-text (fn [{:keys [ctx/world]}]
                                    (info/text skill world))}
                   skin))
  ctx)

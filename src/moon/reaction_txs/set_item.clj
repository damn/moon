(ns moon.reaction-txs.set-item
  (:require [moon.info :as info]
            [moon.textures :as textures]
            [moon.ui :as ui]))

(defn do!
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid cell item]
  (when (:entity/player? @eid)
    (ui/set-item! stage cell
                  {:texture-region (textures/texture-region textures (:entity/image item))
                   :tooltip-text (info/text item nil)}
                  skin))
  ctx)

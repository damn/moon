(ns moon.reaction-txs.set-item
  (:require [moon.graphics :as graphics]
            [moon.ui :as ui]
            [moon.world.info :as info]))

(defn do!
  [{:keys [ctx/graphics
           ctx/skin
           ctx/stage]
    :as ctx}
   eid cell item]
  (when (:entity/player? @eid)
    (ui/set-item! stage cell
                  {:texture-region (graphics/texture-region graphics (:entity/image item))
                   :tooltip-text (info/text item nil)}
                  skin))
  ctx)

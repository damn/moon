(ns moon.world.tx.spawn-item)

(defn do! [_ctx position item]
  [[:tx/spawn-entity
    {:entity/body {:position position
                   :width 0.75
                   :height 0.75
                   :z-order :z-order/on-ground}
     :entity/image (:entity/image item)
     :entity/item item
     :entity/clickable {:type :clickable/item
                        :text (:property/pretty-name item)}}]])

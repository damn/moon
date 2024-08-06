(ns tx.entity.audiovisual
  (:require [api.context :as ctx]
            [api.tx :refer [transact!]]))

(defmethod transact! :tx.entity/audiovisual [[_ position id] ctx]
  ; assert property of type audiovisual
  (let [{:keys [property/sound
                property/animation]} (ctx/get-property ctx id)]
    [[:tx/sound sound]
     [:tx/create #:entity {:position position
                           :animation animation
                           :z-order :z-order/effect
                           :delete-after-animation-stopped? true}]]))
(ns ctx.tx.spawn-creature
  (:require [clojure.safe-merge :refer [safe-merge]]))

(defn do!
  [_ctx
   {:keys [position
           creature-property
           components]}]
  (assert creature-property)
  [[:tx/spawn-entity
    (-> creature-property
        (assoc :entity/body (let [{:keys [body/width body/height #_body/flying?]} (:entity/body creature-property)]
                              {:position position
                               :width  width
                               :height height
                               :collides? true
                               :z-order :z-order/ground #_(if flying? :z-order/flying :z-order/ground)}))
        (assoc :entity/destroy-audiovisual :audiovisuals/creature-die)
        (safe-merge components))]])

(ns tx.audiovisual
  (:require [moon.db.build :refer [build]]))

(defn do!
  [{:keys [ctx/db]} position audiovisual]
  (let [{:keys [tx/sound
                entity/animation]} (if (keyword? audiovisual)
                                     (build db audiovisual)
                                     audiovisual)]
    [[:tx/sound sound]
     [:tx/spawn-effect
      position
      {:entity/animation (assoc animation :delete-after-stopped? true)}]]))

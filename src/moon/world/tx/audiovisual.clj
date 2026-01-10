(ns moon.world.tx.audiovisual
  (:require [moon.db :as db]))

(defn do!
  [{:keys [ctx/db]} position audiovisual]
  (let [{:keys [tx/sound
                entity/animation]} (if (keyword? audiovisual)
                                     (db/build db audiovisual)
                                     audiovisual)]
    [[:tx/sound sound]
     [:tx/spawn-effect
      position
      {:entity/animation (assoc animation :delete-after-stopped? true)}]]))

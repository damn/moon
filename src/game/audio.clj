(ns game.audio
  (:require [entity.spawn :refer [spawn-entity!]]
            [game.spawn :refer [spawn-effect]]
            [moon.db :as db]))

(defn audiovisual! [db schemas world elapsed-time play-sound! skin stage textures z-orders minimum-size position audiovisual]
  (let [{:keys [tx/sound entity/animation]} (if (keyword? audiovisual)
                                             (db/build db schemas audiovisual)
                                             audiovisual)]
    (play-sound! sound)
    (spawn-entity! world elapsed-time z-orders minimum-size
                   (spawn-effect position
                                 {:entity/animation (assoc animation :delete-after-stopped? true)}))))

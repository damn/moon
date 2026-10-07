(ns game.audio
  (:require [entity.spawn :refer [spawn-entity!]]
            [game.spawn :refer [spawn-effect]]
            [moon.db :as db])
  (:import (com.badlogic.gdx.audio Sound)))

(defn play-sound! [sounds sound-name]
  (assert (contains? sounds sound-name) (str sound-name))
  (.play ^Sound (get sounds sound-name)))

(defn audiovisual! [db world elapsed-time audio skin stage textures z-orders minimum-size position audiovisual]
  (let [{:keys [tx/sound entity/animation]} (if (keyword? audiovisual)
                                             (db/build db audiovisual)
                                             audiovisual)]
    (play-sound! audio sound)
    (spawn-entity! world elapsed-time skin stage textures z-orders minimum-size
                   (spawn-effect position
                                 {:entity/animation (assoc animation :delete-after-stopped? true)}))))

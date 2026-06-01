(ns entity.load
  (:require [game.entity :as entity]))

(defmethod entity/destroy :entity/destroy-audiovisual
  [[_ audiovisuals-id] eid]
  [[:tx/audiovisual
    (:body/position (:entity/body @eid))
    audiovisuals-id]])

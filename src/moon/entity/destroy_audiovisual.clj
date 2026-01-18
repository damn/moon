(ns moon.entity.destroy-audiovisual)

(defn destroy
  [[_ audiovisuals-id] eid]
  [[:tx/audiovisual
    (:body/position (:entity/body @eid))
    audiovisuals-id]])

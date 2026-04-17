(ns moon.raycaster)

(defprotocol Raycaster
  (blocked? [_ [start-x start-y] [target-x target-y]]))

(defn line-of-sight? [raycaster source target]
  (not (blocked? raycaster
                 (:body/position (:entity/body source))
                 (:body/position (:entity/body target)))))

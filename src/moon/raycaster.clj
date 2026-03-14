(ns moon.raycaster
  (:import (moon RayCaster)))

(defn blocked? [[arr width height] [start-x start-y] [target-x target-y]]
  (RayCaster/rayBlocked (double start-x)
                        (double start-y)
                        (double target-x)
                        (double target-y)
                        width
                        height
                        arr))

(defn line-of-sight? [raycaster source target]
  (not (blocked? raycaster
                 (:body/position (:entity/body source))
                 (:body/position (:entity/body target)))))

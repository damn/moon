(ns moon.raycaster
  (:require [clojure.math.raycaster :as raycaster]))

(defn blocked? [[arr width height] [start-x start-y] [target-x target-y]]
  (raycaster/blocked? [arr width height] [start-x start-y] [target-x target-y]))

(defn line-of-sight? [raycaster source target]
  (not (blocked? raycaster
                 (:body/position (:entity/body source))
                 (:body/position (:entity/body target)))))

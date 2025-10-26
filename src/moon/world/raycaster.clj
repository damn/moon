(ns moon.world.raycaster
  (:require [clojure.math.raycaster :as raycaster]
            [clojure.math.vector2 :as v]))

(defn blocked? [{:keys [world/raycaster]} start target]
  (raycaster/blocked? raycaster start target))

(defn- create-double-ray-endpositions
  [[start-x start-y]
   [target-x target-y]
   path-w]
  {:pre [(< path-w 0.98)]} ; wieso 0.98??
  (let [path-w (+ path-w 0.02) ;etwas gr�sser damit z.b. projektil nicht an ecken anst�sst
        v (v/direction [start-x start-y]
                       [target-y target-y])
        [normal1 normal2] (v/normal-vectors v)
        normal1 (v/scale normal1 (/ path-w 2))
        normal2 (v/scale normal2 (/ path-w 2))
        start1  (v/add [start-x  start-y]  normal1)
        start2  (v/add [start-x  start-y]  normal2)
        target1 (v/add [target-x target-y] normal1)
        target2 (v/add [target-x target-y] normal2)]
    [start1,target1,start2,target2]))

(defn path-blocked? [{:keys [world/raycaster]} start target path-w]
  (let [[start1,target1,start2,target2] (create-double-ray-endpositions start target path-w)]
    (or
     (raycaster/blocked? raycaster start1 target1)
     (raycaster/blocked? raycaster start2 target2))))

(defn line-of-sight? [{:keys [world/raycaster]} source target]
  (not (raycaster/blocked? raycaster
                           (:body/position (:entity/body source))
                           (:body/position (:entity/body target)))))

(ns moon.raycaster
  (:require [clojure.math.raycaster :as raycaster]))

(defn line-of-sight? [raycaster source target]
  (not (raycaster/blocked? raycaster
                           (:body/position (:entity/body source))
                           (:body/position (:entity/body target)))))

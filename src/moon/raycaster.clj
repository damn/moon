(ns moon.raycaster
  (:require [clojure.math.raycaster :as raycaster]))

(def blocked? raycaster/blocked?)

(defn line-of-sight? [raycaster source target]
  (not (blocked? raycaster
                 (:body/position (:entity/body source))
                 (:body/position (:entity/body target)))))

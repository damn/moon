(ns moon.raycaster.line-of-sight
  (:require [math.raycaster :as raycaster]))

(defn f [this source target]
  (not (raycaster/blocked? this
                           (:body/position (:entity/body source))
                           (:body/position (:entity/body target)))))

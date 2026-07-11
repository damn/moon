(ns clojure.line-of-sight
  (:require [moon.raycaster :as raycaster]))

(defn f [raycaster source target]
  (not (raycaster/blocked? raycaster
                           (:body/position (:entity/body source))
                           (:body/position (:entity/body target)))))

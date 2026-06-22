(ns moon.raycaster.is-blocked
  (:require [clojure.math.raycaster :as raycaster]))

(defn f [this [start-x start-y] [target-x target-y]]
  (raycaster/blocked? this
                      [start-x start-y]
                      [target-x target-y]))

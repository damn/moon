(ns clojure.raycaster-is-blocked
  (:import (clojure RayCaster)))

(defn f [this [start-x start-y] [target-x target-y]]
  (RayCaster/rayBlocked (double start-x)
                        (double start-y)
                        (double target-x)
                        (double target-y)
                        (this 1)
                        (this 2)
                        (this 0)))

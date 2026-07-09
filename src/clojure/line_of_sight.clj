(ns clojure.line-of-sight
  (:import (clojure RayCaster)))

(defn f [this source target]
  (not (RayCaster/rayBlocked (double (first (:body/position (:entity/body source))))
                             (double (second (:body/position (:entity/body source))))
                             (double (first (:body/position (:entity/body target))))
                             (double (second (:body/position (:entity/body target))))
                             (this 1)
                             (this 2)
                             (this 0))))

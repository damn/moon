; :TODO the raycaster is actually a java class okay
; but we are talking here about a data-shape
; which is
; [bool-arr int-width int-height] symbolising the blocked-status
; for light sources????
; - a different name -
; ?
(ns moon.raycaster
  (:import (clojure RayCaster)))

(defn blocked?
  [[bool-arr
    int-width
    int-height]
   [start-x start-y]
   [target-x target-y]]
  (RayCaster/rayBlocked (double start-x)
                        (double start-y)
                        (double target-x)
                        (double target-y)
                        int-width
                        int-height
                        bool-arr))

(defn line-of-sight?
  [raycaster source target]
  (not (blocked? raycaster
                 ; 2 abstraction leakages:
                 ; we know internal of entity
                 ; we know internal of body
                 ; entity/position function
                 ; entity _HAS_ position -> HAS :Entity/body ALWAYS
                 ; => ENTITY DEFINE DATA SHAPE
                 ; => CREATURE/PROJECTILE/ITEM DEFINE
                 (:body/position (:entity/body source))
                 (:body/position (:entity/body target)))))

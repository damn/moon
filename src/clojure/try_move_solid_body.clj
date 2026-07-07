(ns clojure.try-move-solid-body
  (:require [clojure.math :as math]
            [clojure.try-move :as try-move]))

(defn f [grid body entity-id {[vx vy] :direction :as movement}]
  (let [xdir (math/signum (float vx))
        ydir (math/signum (float vy))]
    (or (try-move/f grid body entity-id movement)
        (try-move/f grid body entity-id (assoc movement :direction [xdir 0]))
        (try-move/f grid body entity-id (assoc movement :direction [0 ydir])))))

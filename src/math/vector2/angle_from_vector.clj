(ns math.vector2.angle-from-vector
  (:require [math.vector2.angle-deg :as angle-deg]))

(defn f
  "converts theta of Vector2 to angle from top (top is 0 degree, moving left is 90 degree etc.), counterclockwise"
  [v]
  (angle-deg/f v [0 1]))

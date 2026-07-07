(ns clojure.angle-deg
  (:require [clojure.math :as math]
            [clojure.dot :as dot]
            [clojure.crs :as crs]))

(defn f
  "Returns the angle in degrees of this vector relative to the given reference vector.
  Angles are towards the positive y-axis (counter-clockwise) in the `[0, 360-` range."
  [this reference]
  (let [angle (math/to-degrees
               (math/atan2 (crs/f reference this)
                           (dot/f reference this)))]
    (if (neg? angle)
      (+ angle 360)
      angle)))

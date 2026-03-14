(ns moon.vector2
  (:require [clojure.math :as math]))

(defn scale [[x y] scalar]
  [(* x scalar)
   (* y scalar)])

(defn length [[x y]]
  (math/sqrt (+ (* x x)
                (* y y))))

(defn normalise [[x y :as v]]
  (let [len (length v)]
    (if (zero? len)
      v
      [(/ x len)
       (/ y len)])))

(defn add [v1 v2]
  (mapv + v1 v2))

(defn distance
  [[x1 y1]
   [x2 y2]]
  (let [x-d (- x2 x1)
        y-d (- y2 y1)]
   (math/sqrt (+ (* x-d x-d)
                 (* y-d y-d))) ))

(defn direction [[sx sy] [tx ty]]
  (normalise [(- (float tx) (float sx))
              (- (float ty) (float sy))]))

(defn crs
  "Calculates the 2D cross product between this and the given vector"
  [[this-x this-y] [x y]]
  (- (* this-x y)
     (* this-y x)))

(defn dot
  [[this-x this-y]
   [x y]]
  (+ (* this-x x)
     (* this-y y)))

(defn angle-deg
  "Returns the angle in degrees of this vector relative to the given reference vector.
  Angles are towards the positive y-axis (counter-clockwise) in the `[0, 360-` range."
  [this reference]
  (let [angle (math/to-degrees
               (math/atan2 (crs reference this)
                           (dot reference this)))]
    (if (neg? angle)
      (+ angle 360)
      angle)))

(defn angle-from-vector
  "converts theta of Vector2 to angle from top (top is 0 degree, moving left is 90 degree etc.), counterclockwise"
  [v]
  (angle-deg v [0 1]))

(defn normal-vectors [[x y]]
  [[(- (float y))         x]
   [          y (- (float x))]])

(defn diagonal-direction? [[x y]]
  (and (not (zero? (float x)))
       (not (zero? (float y)))))

(defn add-vs [vs]
  (normalise (reduce add [0 0] vs)))

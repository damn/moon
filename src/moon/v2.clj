(ns moon.v2
  (:require [clojure.math :as math]))

(defn add [v1 v2]
  (mapv + v1 v2))

(defn scale [[x y] scalar]
  [(* x scalar)
   (* y scalar)])

(defn dot
  [[this-x this-y]
   [x y]]
  (+ (* this-x x)
     (* this-y y)))

(defn crs
  "Calculates the 2D cross product between this and the given vector"
  [[this-x this-y] [x y]]
  (- (* this-x y)
     (* this-y x)))

(defn length [[x y]]
  (math/sqrt (+ (* x x)
                (* y y))))

(defn normalise [[x y :as v]]
  (let [len (length v)]
    (if (zero? len)
      v
      [(/ x len)
       (/ y len)])))

(defn normal-vectors [[x y]]
  [[(- (float y))         x]
   [          y (- (float x))]])

(defn direction [[sx sy] [tx ty]]
  (normalise [(- (float tx) (float sx))
              (- (float ty) (float sy))]))

(defn distance
  [[x1 y1]
   [x2 y2]]
  (let [x-d (- x2 x1)
        y-d (- y2 y1)]
    (math/sqrt (+ (* x-d x-d)
                  (* y-d y-d)))))

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

(defn double-ray-endpositions
  [[start-x start-y]
   [target-x target-y]
   path-w]
  {:pre [(< path-w 0.98)]}
  (let [path-w (+ path-w 0.02)
        v (direction [start-x start-y]
                     [target-y target-y])
        [normal1 normal2] (normal-vectors v)
        normal1 (scale normal1 (/ path-w 2))
        normal2 (scale normal2 (/ path-w 2))
        start1  (add [start-x  start-y]  normal1)
        start2  (add [start-x  start-y]  normal2)
        target1 (add [target-x target-y] normal1)
        target2 (add [target-x target-y] normal2)]
    [start1 target1 start2 target2]))

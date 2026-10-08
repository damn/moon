(ns moon.v2
  (:require [clojure.math :as math]))

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

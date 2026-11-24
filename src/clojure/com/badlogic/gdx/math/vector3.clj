(ns clojure.com.badlogic.gdx.math.vector3
  "Encapsulates a 3D vector. Allows chaining operations by returning a reference to itself in all modification methods."
  (:import (com.badlogic.gdx.math Vector3)))

(defn x
  "The x-component of this vector"
  [^Vector3 v]
  (.x v))

(defn y
  "The y-component of this vector"
  [^Vector3 v]
  (.y v))

(defn z
  "The z-component of this vector"
  [^Vector3 v]
  (.z v))

(defn set-x!
  "The x-component of this vector"
  [^Vector3 v x]
  (set! (.x v) x))

(defn set-y!
  "The y-component of this vector"
  [^Vector3 v y]
  (set! (.y v) y))

(defn set-z!
  "The z-component of this vector"
  [^Vector3 v z]
  (set! (.z v) z))

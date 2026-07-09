(ns com.badlogic.gdx.math.vector3
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.math Vector3)))

(defn x [^Vector3 v]
  (.x v))

(defn y [^Vector3 v]
  (.y v))

(defn z [^Vector3 v]
  (.z v))

(defn set-x! [vector3 x]
  (set! (.x ^Vector3 vector3) x))

(defn set-y! [vector3 y]
  (set! (.y ^Vector3 vector3) y))

(defn clojurize [v3] ; TODO remove!
  [(x v3)
   (y v3)
   (z v3)])

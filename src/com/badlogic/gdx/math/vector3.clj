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
  (.setX ^Vector3 vector3 (float x)))

(defn set-y! [vector3 y]
  (.setY ^Vector3 vector3 (float y)))

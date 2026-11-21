(ns clojure.com.badlogic.gdx.math.vector3
  (:import (com.badlogic.gdx.math Vector3)))

(defn x [^Vector3 v]
  (.x v))

(defn y [^Vector3 v]
  (.y v))

(defn z [^Vector3 v]
  (.z v))

(defn set-x! [^Vector3 v x]
  (set! (.x v) x))

(defn set-y! [^Vector3 v y]
  (set! (.y v) y))

(defn set-z! [^Vector3 v z]
  (set! (.z v) z))

(ns gdx.vector3
  (:require [com.badlogic.gdx.math.vector3 :as vector3]))

(defn x [v3]
  (vector3/x v3))

(defn y [v3]
  (vector3/y v3))

(defn z [v3]
  (vector3/z v3))

(defn set-x! [v3 x]
  (vector3/set-x! v3 x))

(defn set-y! [v3 y]
  (vector3/set-y! v3 y))

(defn clojurize [v3]
  [(vector3/x v3) (vector3/y v3) (vector3/z v3)])

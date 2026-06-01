(ns clojure.gdx.math.vector3
  (:import (com.badlogic.gdx.math Vector3)))

(defn ->clj [^Vector3 v3]
  [(.x v3)
   (.y v3)
   (.z v3)])

(defn set-x! [^Vector3 v3 x]
  (set! (.x v3) x))

(defn set-y! [^Vector3 v3 y]
  (set! (.y v3) y))

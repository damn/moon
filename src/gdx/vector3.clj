(ns gdx.vector3
  (:import (com.badlogic.gdx.math Vector3)))

(defn x [v3]
  (.x ^Vector3 v3))

(defn y [v3]
  (.y ^Vector3 v3))

(defn z [v3]
  (.z ^Vector3 v3))

(defn set-x! [v3 x]
  (set! (.x ^Vector3 v3) x))

(defn set-y! [v3 y]
  (set! (.y ^Vector3 v3) y))

(defn clojurize [v3]
  [(.x ^Vector3 v3) (.y ^Vector3 v3) (.z ^Vector3 v3)])

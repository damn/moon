(ns clojure.gdx.vector3.set-y
  (:import (com.badlogic.gdx.math Vector3)))

(defn f [vector3 y]
  (set! (.y ^Vector3 vector3) y))

(ns clojure.gdx.vector3.set-x
  (:import (com.badlogic.gdx.math Vector3)))

(defn f [vector3 x]
  (set! (.x ^Vector3 vector3) x))

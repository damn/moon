(ns gdx.math.vector3.clojurize
  (:require [com.badlogic.gdx.math.vector3 :as vector3]))

(defn f [v3]
  [(vector3/x v3)
   (vector3/y v3)
   (vector3/z v3)])

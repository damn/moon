(ns gdx.math.vector3
  (:require [com.badlogic.gdx.math.vector3 :as vector3]))

(defn clojurize [v3]
  [(vector3/x v3) (vector3/y v3) (vector3/z v3)])

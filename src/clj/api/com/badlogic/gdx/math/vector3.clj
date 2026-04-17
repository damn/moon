(ns clj.api.com.badlogic.gdx.math.vector3
  (:import (com.badlogic.gdx.math Vector3)))

(defn ->clj [^Vector3 v3]
  [(.x v3)
   (.y v3)
   (.z v3)])

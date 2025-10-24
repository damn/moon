(ns clojure.gdx.math.vector3
  (:import (com.badlogic.gdx.math Vector3)))

(defn clojurize [^Vector3 v3]
  [(.x v3)
   (.y v3)
   (.z v3)])

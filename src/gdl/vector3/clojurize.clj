(ns gdl.vector3.clojurize
  (:import (com.badlogic.gdx.math Vector3)))

(defn f [^Vector3 v3]
  [(.x v3)
   (.y v3)
   (.z v3)])

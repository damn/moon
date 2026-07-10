(ns gdl.math.frustum
  (:require [com.badlogic.gdx.math.frustum :as frustum]))

(defn plane-points [& args]
  (apply frustum/planePoints args))

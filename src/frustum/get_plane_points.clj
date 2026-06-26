(ns frustum.get-plane-points
  (:require [com.badlogic.gdx.math.frustum :as frustum]))

(defn get-plane-points [frustum]
  (frustum/plane-points frustum))

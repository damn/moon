(ns gdl.frustum.get-plane-points
  (:import (com.badlogic.gdx.math Frustum)))

(defn get-plane-points [^Frustum frustum]
  (.planePoints frustum))

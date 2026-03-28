(ns clj.api.com.badlogic.gdx.math.frustum
  (:import (com.badlogic.gdx.math Frustum)))

(defn plane-points [^Frustum frustum]
  (.planePoints frustum))

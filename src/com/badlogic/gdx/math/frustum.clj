(ns com.badlogic.gdx.math.frustum
  (:import (com.badlogic.gdx.math Frustum)))

; TODO planePoints name?
(defn plane-points [^Frustum frustum]
  (.planePoints frustum))

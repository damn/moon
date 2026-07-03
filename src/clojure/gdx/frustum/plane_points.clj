(ns clojure.gdx.frustum.plane-points
  (:import (com.badlogic.gdx.math Frustum)))

(defn f [^Frustum frustum]
  (.planePoints frustum))

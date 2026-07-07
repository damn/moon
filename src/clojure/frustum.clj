(ns clojure.frustum
  (:import (com.badlogic.gdx.math Frustum)))

(defn plane-points [^Frustum frustum]
  (.planePoints frustum))

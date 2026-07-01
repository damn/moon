(ns orthographic-camera.frustum
  (:require [clojure.gdx.orthographic-camera.frustum :as frustum]
            [gdx.math.vector3.clojurize :as clojurize])
  (:import (com.badlogic.gdx.math Frustum)))

(defn frustum [camera]
  (let [plane-points (mapv clojurize/f (.planePoints ^Frustum (frustum/f camera)))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        right-x  (apply max (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))
        top-y    (apply max (map second frustum-points))]
    [left-x right-x bottom-y top-y]))

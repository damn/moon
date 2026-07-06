(ns gdx.graphics.orthographic-camera.frustum
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.math.frustum :as frustum]
            [com.badlogic.gdx.math.vector3 :as vector3]))

(defn frustum [camera]
  (let [plane-points (mapv vector3/clojurize (frustum/plane-points (orthographic-camera/frustum camera)))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        right-x  (apply max (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))
        top-y    (apply max (map second frustum-points))]
    [left-x right-x bottom-y top-y]))

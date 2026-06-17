(ns gdx.orthographic-camera.frustum
  (:require [com.badlogic.gdx.math.vector3 :refer [->clj]]
            [com.badlogic.gdx.graphics.orthographic-camera.get-frustum :refer [get-frustum]]
            [com.badlogic.gdx.math.frustum.get-plane-points :refer [get-plane-points]]))

(defn frustum [camera]
  (let [plane-points (mapv ->clj (get-plane-points (get-frustum camera)))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        right-x  (apply max (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))
        top-y    (apply max (map second frustum-points))]
    [left-x right-x bottom-y top-y]))

(ns orthographic-camera.frustum
  (:require [clojure.gdx :as gdx]))

(defn frustum [camera]
  (let [plane-points (mapv gdx/vector3-clojurize (gdx/camera-frustum-plane-points camera))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        right-x  (apply max (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))
        top-y    (apply max (map second frustum-points))]
    [left-x right-x bottom-y top-y]))

(ns orthographic-camera.frustum
  (:require [com.badlogic.gdx.math.frustum :as frustum]
            [clojure.gdx.orthographic-camera.frustum :as camera-frustum]
            [clojure.gdx.vector3.clojurize :as clojurize]))

(defn frustum [camera]
  (let [plane-points (mapv clojurize/f (frustum/plane-points (camera-frustum/f camera)))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        right-x  (apply max (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))
        top-y    (apply max (map second frustum-points))]
    [left-x right-x bottom-y top-y]))

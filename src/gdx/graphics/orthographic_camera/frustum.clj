(ns gdx.graphics.orthographic-camera.frustum
  (:require [gdx.to-clj :refer [->clj]])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn frustum [^OrthographicCamera camera]
  (let [plane-points (mapv ->clj (.planePoints (.frustum camera)))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        right-x  (apply max (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))
        top-y    (apply max (map second frustum-points))]
    [left-x right-x bottom-y top-y]))

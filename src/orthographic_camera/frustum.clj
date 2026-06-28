(ns orthographic-camera.frustum
  (:require [gdx.math.vector3.clojurize :as clojurize]
            [frustum.get-plane-points :refer [get-plane-points]])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn frustum [^OrthographicCamera camera]
  (let [plane-points (mapv clojurize/f (get-plane-points (.frustum camera)))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        right-x  (apply max (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))
        top-y    (apply max (map second frustum-points))]
    [left-x right-x bottom-y top-y]))

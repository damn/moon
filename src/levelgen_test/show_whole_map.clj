(ns levelgen-test.show-whole-map
  (:require [clojure.gdx :as gdx]
            [orthographic-camera.set-position :refer [set-position!]]
            [orthographic-camera.calculate-zoom :refer [calculate-zoom]]
            [orthographic-camera.set-zoom :refer [set-zoom!]]))

(defn f!
  [{:keys [ctx/camera
           ctx/tiled-map]}]
  (let [props (gdx/tiled-map-get-properties tiled-map)]
    (set-position! camera
                   [(/ (gdx/map-properties-get props "width") 2)
                    (/ (gdx/map-properties-get props "height") 2)])
    (set-zoom! camera
               (calculate-zoom camera
                               {:left [0 0]
                                :top [0 (gdx/map-properties-get props "height")]
                                :right [(gdx/map-properties-get props "width") 0]
                                :bottom [0 0]}))))

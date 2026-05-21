(ns clojure.gdx.graphics.orthographic-camera
  (:require [clojure.gdx.math.vector3 :as vector3]
            [clojure.app :as app]
            [clojure.graphics.orthographic-camera :as camera])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(.bindRoot #'app/orthographic-camera
           (fn
             [{:keys [y-down?
                      world-width
                      world-height]}]
             (doto (OrthographicCamera.)
               (.setToOrtho y-down? world-width world-height))))

(extend-type OrthographicCamera
  camera/OrthographicCamera
  (viewport-width [camera]
    (.viewportWidth camera))

  (viewport-height [camera]
    (.viewportHeight camera))

  (combined [camera]
    (.combined camera))

  (zoom [camera]
    (.zoom camera))

  (frustum [camera]
    (let [plane-points (mapv vector3/->clj (.planePoints (.frustum camera)))
          frustum-points (take 4 plane-points)
          left-x   (apply min (map first  frustum-points))
          right-x  (apply max (map first  frustum-points))
          bottom-y (apply min (map second frustum-points))
          top-y    (apply max (map second frustum-points))]
      [left-x right-x bottom-y top-y]))

  (position [camera]
    (vector3/->clj (.position camera)))

  (set-position! [camera [x y]]
    (set! (.x (.position camera)) x)
    (set! (.y (.position camera)) y)
    (.update camera))

  (set-zoom! [camera amount]
    (set! (.zoom camera) amount)
    (.update camera)))

(ns clojure.gdx.orthographic-camera
  (:require [clojure.gdx.math.vector3 :as vector3]
            clojure.graphics.orthographic-camera)
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn create []
  (OrthographicCamera.))

(defn set-to-ortho! [^OrthographicCamera camera y-down? viewport-width viewport-height]
  (.setToOrtho camera y-down? viewport-width viewport-height))

(extend-type OrthographicCamera
  clojure.graphics.orthographic-camera/Camera
  (combined [camera]
    (.combined camera))

  (set-position! [camera [x y]]
    (set! (.x (.position camera)) x)
    (set! (.y (.position camera)) y)
    (.update camera))

  (set-zoom! [camera amount]
    (set! (.zoom camera) amount)
    (.update camera))

  (zoom [camera]
    (.zoom camera))

  (frustum* [camera]
    (mapv vector3/->clj (.planePoints (.frustum camera))))

  (position [camera]
    (vector3/->clj (.position camera)))

  (viewport-width [camera]
    (.viewportHeight camera))

  (viewport-height [camera]
    (.viewportHeight camera)))

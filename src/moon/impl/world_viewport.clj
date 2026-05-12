(ns moon.impl.world-viewport
  (:require [clojure.gdx.utils.viewport :as viewport]
            [clojure.graphics.orthographic-camera :as camera]
            [com.badlogic.gdx.math.vector2 :as vector2]
            moon.camera)
  (:import (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create
  [{:keys [ctx/world-unit-scale]}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (FitViewport. world-width
                  world-height
                  (doto (OrthographicCamera.)
                    (.setToOrtho false world-width world-height)))))

(extend-type FitViewport
  moon.camera/Camera
  (zoom [this]
    (camera/zoom (viewport/camera this)))

  (visible-tiles [this]
    (camera/visible-tiles (viewport/camera this)))

  (frustum [this]
    (camera/frustum (viewport/camera this)))

  (inc-zoom! [this amount]
    (camera/inc-zoom! (viewport/camera this) amount))

  (set-position! [this position]
    (camera/set-position! (viewport/camera this)
                          position))

  (position [this]
    (camera/position (viewport/camera this)))

  (combined [this]
    (camera/combined (viewport/camera this)))

  viewport/Viewport
  (camera [viewport]
    (.getCamera viewport))

  (world-width [viewport]
    (.getWorldWidth viewport))

  (world-height [viewport]
    (.getWorldHeight viewport))

  (update! [viewport screen-width screen-height center-camera?]
    (.update viewport screen-width screen-height center-camera?))

  (unproject [viewport position]
    (-> viewport
        (.unproject (vector2/->java position))
        vector2/->clj)))

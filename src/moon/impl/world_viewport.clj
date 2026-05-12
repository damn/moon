(ns moon.impl.world-viewport
  (:require [clojure.gdx.utils.viewport :as viewport]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.math.vector2 :as vector2])
  (:import (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create
  [{:keys [ctx/world-unit-scale]}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (FitViewport. world-width
                  world-height
                  (doto (orthographic-camera/create)
                    (orthographic-camera/set-to-ortho! false world-width world-height)))))

(extend-type FitViewport
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

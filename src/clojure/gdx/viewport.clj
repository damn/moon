(ns clojure.gdx.viewport
  (:require [clojure.gdx.math.vector2 :as vector2]
            clojure.graphics.viewport)
  (:import (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create [world-width world-height camera]
  (FitViewport. world-width world-height camera))

(extend-type FitViewport
  clojure.graphics.viewport/Viewport
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

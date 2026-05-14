(ns moon.impl.world-viewport
  (:require [moon.world-viewport :as viewport]
            [moon.gdx.orthographic-camera :as camera-impl]
            [com.badlogic.gdx.math.vector2 :as vector2])
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create
  [{:keys [ctx/world-unit-scale]}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (proxy [FitViewport ILookup] [world-width
                                  world-height
                                  (camera-impl/create {:y-down? false
                                                       :world-width world-width
                                                       :world-height world-height})]
      (valAt [k]
        (case k
          :viewport/camera (.getCamera this)
          ))
      )))

(extend-type FitViewport
  viewport/Viewport
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

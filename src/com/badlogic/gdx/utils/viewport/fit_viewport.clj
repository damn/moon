(ns com.badlogic.gdx.utils.viewport.fit-viewport
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [clojure.app :as app]
            [clojure.utils.viewport :as viewport])
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(.bindRoot #'app/fit-viewport
           (fn
             ([width height]
              (proxy [FitViewport ILookup] [width height]
                (valAt [k]
                  (case k
                    :viewport/camera       (FitViewport/.getCamera      this)
                    :viewport/world-width  (FitViewport/.getWorldWidth  this)
                    :viewport/world-height (FitViewport/.getWorldHeight this)))))
             ([width height camera]
              (proxy [FitViewport ILookup] [width height camera]
                (valAt [k]
                  (case k
                    :viewport/camera       (FitViewport/.getCamera      this)
                    :viewport/world-width  (FitViewport/.getWorldWidth  this)
                    :viewport/world-height (FitViewport/.getWorldHeight this)))))))

(extend-type FitViewport
  viewport/Viewport
  (update! [viewport screen-width screen-height center-camera?]
    (.update viewport screen-width screen-height center-camera?))

  (unproject [viewport position]
    (-> viewport
        (.unproject (vector2/->java position))
        vector2/->clj)))

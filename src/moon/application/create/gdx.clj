(ns moon.application.create.gdx
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [moon.gdx :as gdx]
            [moon.viewport :as viewport])
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn step [ctx]
  (extend-type (class ctx)
    gdx/Gdx
    (fit-viewport [_ width height camera]
      (proxy [FitViewport ILookup] [width height camera]
        (valAt [k]
          (case k
            :viewport/camera (.getCamera this)
            ))))
    )
  ctx)

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

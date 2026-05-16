(ns com.badlogic.gdx.utils.viewport.fit-viewport
  (:require [com.badlogic.gdx.math.vector2 :as vector2])
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create [width height camera]
  (proxy [FitViewport ILookup] [width height camera]
    (valAt [k]
      (case k
        :viewport/camera       (FitViewport/.getCamera      this)
        :viewport/world-width  (FitViewport/.getWorldWidth  this)
        :viewport/world-height (FitViewport/.getWorldHeight this)))))

(defn update! [^FitViewport viewport screen-width screen-height center-camera?]
  (.update viewport screen-width screen-height center-camera?))

(defn unproject [^FitViewport viewport position]
  (-> viewport
      (.unproject (vector2/->java position))
      vector2/->clj))

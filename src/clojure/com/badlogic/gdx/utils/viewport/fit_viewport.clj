(ns clojure.com.badlogic.gdx.utils.viewport.fit-viewport
  "A ScalingViewport that uses fit so it keeps the aspect ratio by scaling the world up to fit the screen, adding
  black bars (letterboxing) for the remaining space."
  (:import (com.badlogic.gdx.graphics Camera)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create
  "Creates a new viewport using a new [[clojure.com.badlogic.gdx.graphics.orthographic-camera]]."
  ([world-width world-height]
   (FitViewport. (float world-width) (float world-height)))
  ([world-width world-height ^Camera camera]
   (FitViewport. (float world-width) (float world-height) camera)))

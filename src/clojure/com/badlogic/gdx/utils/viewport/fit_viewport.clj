(ns clojure.com.badlogic.gdx.utils.viewport.fit-viewport
  (:import (com.badlogic.gdx.graphics Camera)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create
  ([world-width world-height]
   (FitViewport. (float world-width) (float world-height)))
  ([world-width world-height ^Camera camera]
   (FitViewport. (float world-width) (float world-height) camera)))

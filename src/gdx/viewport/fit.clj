(ns gdx.viewport.fit
  (:import (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create
  ([world-width world-height]
   (FitViewport. (float world-width) (float world-height)))
  ([world-width world-height camera]
   (FitViewport. (float world-width) (float world-height) camera)))

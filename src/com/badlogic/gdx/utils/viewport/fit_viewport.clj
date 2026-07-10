(ns com.badlogic.gdx.utils.viewport.fit-viewport
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.utils.viewport FitViewport)))

(defn new
  ([width height]
   (FitViewport. (float width) (float height)))
  ([width height camera]
   (FitViewport. (float width) (float height) camera)))

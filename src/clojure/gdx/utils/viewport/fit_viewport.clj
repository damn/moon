(ns clojure.gdx.utils.viewport.fit-viewport
  (:import (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create [world-width world-height camera]
  (FitViewport. world-width world-height camera))

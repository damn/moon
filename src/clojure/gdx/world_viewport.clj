(ns clojure.gdx.world-viewport
  (:require [clj.api.com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [clj.api.com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn create [world-width world-height]
  (fit-viewport/create world-width
                       world-height
                       (doto (orthographic-camera/create)
                         (orthographic-camera/set-to-ortho! false world-width world-height))))

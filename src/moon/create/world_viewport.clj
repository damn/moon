(ns moon.create.world-viewport
  (:require [clj.api.com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera])
  (:import (com.badlogic.gdx.utils.viewport FitViewport)))

(defn step
  [{:keys [ctx/world-unit-scale]
    :as ctx}
   {:keys [width height]}]
  (assoc ctx :ctx/world-viewport
         (let [world-width  (* width world-unit-scale)
               world-height (* height world-unit-scale)]
           (FitViewport. world-width
                         world-height
                         (doto (orthographic-camera/create)
                           (orthographic-camera/set-to-ortho! false world-width world-height))))))

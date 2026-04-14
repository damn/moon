(ns moon.create.world-viewport
  (:require [clj.api.com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [clj.api.com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn- world-viewport [world-width world-height]
  (fit-viewport/create world-width
                       world-height
                       (doto (orthographic-camera/create)
                         (orthographic-camera/set-to-ortho! false world-width world-height))))

(defn step
  [{:keys [ctx/world-unit-scale]
    :as ctx}
   {:keys [width height]}]
  (assoc ctx :ctx/world-viewport (world-viewport (* width world-unit-scale)
                                                 (* height world-unit-scale))))

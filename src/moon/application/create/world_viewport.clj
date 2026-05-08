(ns moon.application.create.world-viewport
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [clojure.graphics.orthographic-camera :as camera]
            [clojure.gdx.utils.viewport :as viewport]
            moon.camera))

(defn step
  [{:keys [ctx/world-unit-scale]
    :as ctx}]
  (extend-type (class ctx)
    moon.camera/Camera
    (zoom [{:keys [ctx/world-viewport]}]
      (camera/zoom (viewport/camera world-viewport)))

    (visible-tiles [{:keys [ctx/world-viewport]}]
      (camera/visible-tiles (viewport/camera world-viewport)))

    (frustum [{:keys [ctx/world-viewport]}]
      (camera/frustum (viewport/camera world-viewport)))

    (inc-zoom! [{:keys [ctx/world-viewport]} amount]
      (camera/inc-zoom! (viewport/camera world-viewport) amount))

    (set-position! [{:keys [ctx/world-viewport]} position]
      (camera/set-position! (viewport/camera world-viewport)
                            position))

    (position [{:keys [ctx/world-viewport]}]
      (camera/position (viewport/camera world-viewport)))

    (combined [{:keys [ctx/world-viewport]}]
      (camera/combined (viewport/camera world-viewport)))
    )
  (assoc ctx :ctx/world-viewport (let [world-width  (* 1440 world-unit-scale)
                                       world-height (* 900  world-unit-scale)]
                                   (fit-viewport/create world-width
                                                        world-height
                                                        (doto (orthographic-camera/create)
                                                          (orthographic-camera/set-to-ortho! false world-width world-height))))))

(ns ctx.world-viewport
  (:require
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera] [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn step
  [{:keys [ctx/world-unit-scale]}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (fit-viewport/create world-width
                         world-height
                         (doto (orthographic-camera/new)
                           (orthographic-camera/set-to-ortho! false world-width world-height)))))

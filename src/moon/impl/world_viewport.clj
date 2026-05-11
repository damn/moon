(ns moon.impl.world-viewport
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [clojure.gdx.utils.viewport :as viewport]))

(defn create
  [{:keys [ctx/world-unit-scale]}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (viewport/create world-width
                     world-height
                     (doto (orthographic-camera/create)
                       (orthographic-camera/set-to-ortho! false world-width world-height)))))

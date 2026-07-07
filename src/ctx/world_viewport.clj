(ns ctx.world-viewport
  (:require [clojure.orthographic-camera :as orthographic-camera]
            [clojure.fit-viewport :as fit-viewport]))

(defn step
  [{:keys [ctx/world-unit-scale]}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (fit-viewport/create world-width
                         world-height
                         (doto (orthographic-camera/new)
                           (orthographic-camera/set-to-ortho! false world-width world-height)))))

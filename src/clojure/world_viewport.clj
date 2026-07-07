(ns clojure.world-viewport
  (:require [clojure.fit-viewport :as fit-viewport]
            [clojure.orthographic-camera :as orthographic-camera]
            [clojure.world-unit-scale :as world-unit-scale]))

(defn step [_ctx]
  (let [world-width  (* 1440 world-unit-scale/world-unit-scale)
        world-height (* 900  world-unit-scale/world-unit-scale)]
    (fit-viewport/create world-width
                         world-height
                         (doto (orthographic-camera/new)
                           (orthographic-camera/set-to-ortho! false world-width world-height)))))

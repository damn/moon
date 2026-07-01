(ns ctx.world-viewport
  (:require [clojure.gdx.fit-viewport :as fit-viewport]
            [clojure.gdx.orthographic-camera.new :as new-camera]
            [clojure.gdx.orthographic-camera.set-to-ortho :as set-to-ortho!]))

(defn step
  [{:keys [ctx/world-unit-scale]}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (fit-viewport/create world-width
                         world-height
                         (doto (new-camera/f)
                           (set-to-ortho!/f! false world-width world-height)))))

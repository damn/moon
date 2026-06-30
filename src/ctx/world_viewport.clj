(ns ctx.world-viewport
  (:require [clojure.gdx :as gdx]))

(defn step
  [{:keys [ctx/world-unit-scale]}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (gdx/fit-viewport world-width
                      world-height
                      (doto (gdx/orthographic-camera)
                        (gdx/camera-set-to-ortho! false world-width world-height)))))

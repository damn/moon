(ns ctx.world-viewport
  (:require [clojure.gdx :as gdx])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn step
  [{:keys [ctx/world-unit-scale]}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (gdx/fit-viewport world-width
                      world-height
                      (doto (OrthographicCamera.)
                        (.setToOrtho false world-width world-height)))))

(ns ctx.world-viewport
  (:require [viewport.fit-viewport :as fit-viewport])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn step
  [{:keys [ctx/world-unit-scale]}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (fit-viewport/create world-width
                         world-height
                         (doto (OrthographicCamera.)
                           (.setToOrtho false world-width world-height)))))

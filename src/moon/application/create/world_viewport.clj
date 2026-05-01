(ns moon.application.create.world-viewport
  (:require [clojure.graphics.viewport :as viewport])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn step
  [{:keys [ctx/world-unit-scale]
    :as ctx}]
  (assoc ctx :ctx/world-viewport (let [world-width  (* 1440 world-unit-scale)
                                       world-height (* 900  world-unit-scale)]
                                   (viewport/create world-width
                                                    world-height
                                                    (doto (OrthographicCamera.)
                                                      (.setToOrtho false world-width world-height))))))

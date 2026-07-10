(ns clojure.moon.create-world-viewport
  (:require [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [clojure.moon.world-unit-scale :refer [world-unit-scale]]))

(defn f [ctx]
  (assoc ctx
         :ctx/world-viewport (let [world-width (* 1440 world-unit-scale)
                                   world-height (* 900 world-unit-scale)]
                               (fit-viewport/new world-width
                                                    world-height
                                                    (doto (orthographic-camera/new)
                                                      (orthographic-camera/setToOrtho false world-width world-height))))))

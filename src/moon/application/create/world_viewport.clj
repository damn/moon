(ns moon.application.create.world-viewport
  (:require [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.viewport :as viewport]))

(defn step [{:keys [ctx/world-unit-scale] :as ctx}]
  (assoc ctx :ctx/world-viewport
         (let [world-width  (* 1440  world-unit-scale)
               world-height (* 900 world-unit-scale)]
           (viewport/create world-width
                            world-height
                            (doto (camera/create)
                              (camera/set-to-ortho! false world-width world-height))))))

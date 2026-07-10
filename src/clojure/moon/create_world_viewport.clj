(ns clojure.moon.create-world-viewport
  (:require [gdl.fit-viewport :as fit-viewport]
            [gdl.orthographic-camera :as orthographic-camera]
            [clojure.moon.world-unit-scale :refer [world-unit-scale]]))

(defn f [ctx]
  (assoc ctx
         :ctx/world-viewport (let [world-width (* 1440 world-unit-scale)
                                   world-height (* 900 world-unit-scale)]
                               (fit-viewport/create world-width
                                                    world-height
                                                    (doto (orthographic-camera/new)
                                                      (orthographic-camera/set-to-ortho! false world-width world-height))))))

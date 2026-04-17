(ns moon.create.world-viewport
  (:require [clojure.gdx.orthographic-camera :as orthographic-camera]
            [clojure.gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn step
  [{:keys [ctx/world-unit-scale]
    :as ctx}
   {:keys [width height]}]
  (assoc ctx :ctx/world-viewport
         (let [world-width (* width world-unit-scale)
               world-height (* height world-unit-scale)]
           (fit-viewport/create world-width
                                world-height
                                (doto (orthographic-camera/create)
                                  (orthographic-camera/set-to-ortho! false world-width world-height))))))

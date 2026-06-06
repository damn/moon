(ns create.world-viewport
  (:require [gdx.graphics.orthographic-camera :as camera]
            [clojure.fit-viewport :as fit-viewport]))

(defn step
  [{:keys [ctx/world-unit-scale]}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (fit-viewport/create world-width
                         world-height
                         (camera/create
                          {:y-down? false
                           :world-width world-width
                           :world-height world-height}))))

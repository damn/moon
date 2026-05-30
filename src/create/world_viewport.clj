(ns create.world-viewport
  (:require [gdx.graphics.orthographic-camera :as camera]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn step
  [{:keys [ctx/world-unit-scale]
    :as ctx}]
  (assoc ctx :ctx/world-viewport (let [world-width  (* 1440 world-unit-scale)
                                       world-height (* 900  world-unit-scale)]
                                   (fit-viewport/create world-width
                                                        world-height
                                                        (camera/create
                                                         {:y-down? false
                                                          :world-width world-width
                                                          :world-height world-height})))))

(ns moon.impl.world-viewport
  (:require [moon.gdx.viewport :as viewport]
            [moon.gdx.orthographic-camera :as camera]))

(defn create
  [{:keys [ctx/world-unit-scale]}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (viewport/create world-width
                     world-height
                     (camera/create {:y-down? false
                                     :world-width world-width
                                     :world-height world-height}))))

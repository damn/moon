(ns moon.impl.world-viewport
  (:require [moon.gdx :as gdx]
            [moon.gdx.orthographic-camera :as camera]))

(defn create
  [{:keys [ctx/world-unit-scale]
    :as ctx}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (gdx/fit-viewport ctx
                      world-width
                      world-height
                      (camera/create {:y-down? false
                                      :world-width world-width
                                      :world-height world-height}))))

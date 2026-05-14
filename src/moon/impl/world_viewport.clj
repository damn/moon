(ns moon.impl.world-viewport
  (:require [moon.gdx :as gdx]))

(defn create
  [{:keys [ctx/world-unit-scale]
    :as ctx}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (gdx/fit-viewport ctx
                      world-width
                      world-height
                      (gdx/orthographic-camera ctx
                                               {:y-down? false
                                                :world-width world-width
                                                :world-height world-height}))))

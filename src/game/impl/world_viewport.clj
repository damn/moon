(ns game.impl.world-viewport
  (:require [com.badlogic.gdx.gdx :as gdx]))

(defn create
  [{:keys [ctx/world-unit-scale]}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (gdx/fit-viewport world-width
                      world-height
                      (gdx/orthographic-camera {:y-down? false
                                                :world-width world-width
                                                :world-height world-height}))))

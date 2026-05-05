(ns moon.application.create.stage
  (:require [clojure.gdx.scene2d.stage :as stage]
            [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn step
  [{:keys [ctx/app
           ctx/batch]
    :as ctx}]
  (assoc ctx :ctx/stage (let [stage (stage/create (fit-viewport/create 1440 900 (orthographic-camera/create))
                                                  batch)]
                          (input/set-processor! (app/input app) stage)
                          stage)))

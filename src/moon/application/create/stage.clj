(ns moon.application.create.stage
  (:require [clojure.gdx.scene2d.stage :as stage]
            [clojure.input :as input])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn step
  [{:keys [ctx/app
           ctx/batch]
    :as ctx}]
  (assoc ctx :ctx/stage (let [stage (stage/create (FitViewport. 1440 900 (OrthographicCamera.))
                                                  batch)]
                          (input/set-processor! (.getInput app) stage)
                          stage)))

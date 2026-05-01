(ns moon.application.create.stage
  (:require [clojure.gdx.scene2d.stage :as stage]
            [clojure.graphics.viewport :as viewport]
            [clojure.input :as input])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn step
  [{:keys [ctx/app
           ctx/batch]
    :as ctx}]
  (assoc ctx :ctx/stage (let [stage (stage/create (viewport/create 1440 900 (OrthographicCamera.))
                                                  batch)]
                          (input/set-processor! (.getInput app) stage)
                          stage)))

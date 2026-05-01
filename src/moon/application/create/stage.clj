(ns moon.application.create.stage
  (:require [clojure.gdx.scene2d.stage :as stage]
            [clojure.input :as input]))

(defn step
  [{:keys [ctx/app
           ctx/batch
           ctx/ui-viewport]
    :as ctx}]
  (assoc ctx :ctx/stage (let [stage (stage/create ui-viewport batch)]
                          (input/set-processor! (.getInput app) stage)
                          stage)))

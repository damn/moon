(ns create.stage
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.input :as input]
            [clojure.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [clojure.gdx.scene2d.stage :as stage]))

(defn step
  [{:keys [ctx/app
           ctx/batch]
    :as ctx}]
  (assoc ctx :ctx/stage
         (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
           (input/set-processor! (app/input app) stage)
           stage)))

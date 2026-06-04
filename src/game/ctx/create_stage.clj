(ns game.ctx.create-stage
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.input :as input]
            [clojure.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [clojure.gdx.scene2d.stage :as stage]))

(defn step
  [{:keys [ctx/app ctx/batch]} width height]
  (let [stage (stage/create (fit-viewport/create width height) batch)]
    (input/set-processor! (app/input app) stage)
    stage))

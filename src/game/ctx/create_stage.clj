(ns game.ctx.create-stage
  (:require [gdx.application :as app]
            [gdx.input :as input]
            [gdx.utils.fit-viewport :as fit-viewport]
            [gdx.scene2d.stage :as stage]))

(defn create-stage
  [{:keys [ctx/app ctx/batch]} width height]
  (let [stage (stage/create (fit-viewport/create width height) batch)]
    (input/set-processor! (app/input app) stage)
    stage))

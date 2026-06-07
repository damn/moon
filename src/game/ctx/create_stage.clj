(ns game.ctx.create-stage
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.utils.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]))

(defn create-stage
  [{:keys [ctx/app ctx/batch]} width height]
  (let [stage (stage/create (fit-viewport/create width height) batch)]
    (input/set-processor! (app/input app) stage)
    stage))

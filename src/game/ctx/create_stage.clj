(ns game.ctx.create-stage
  (:require [com.badlogic.gdx.input.set-processor :as set-processor!]
            [com.badlogic.gdx.utils.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]))

(defn create-stage
  [{:keys [ctx/input
           ctx/batch]} width height]
  (let [stage (stage/create (fit-viewport/create width height) batch)]
    (set-processor!/f input stage)
    stage))

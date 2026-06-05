(ns game.ctx.create-stage
  (:require [clojure.application :as app]
            [clojure.input :as input]
            [clojure.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [clojure.scene2d.stage :as stage]))

(defn create-stage
  [{:keys [ctx/app ctx/batch]} width height]
  (let [stage (stage/create (fit-viewport/create width height) batch)]
    (input/set-processor! (app/input app) stage)
    stage))

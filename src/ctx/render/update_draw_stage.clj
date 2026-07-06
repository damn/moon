(ns ctx.render.update-draw-stage
  (:require [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [gdx.scene2d.stage :refer [set-ctx!]]))

(defn step
  [{:keys [ctx/stage] :as ctx}]
  (set-ctx! stage ctx)
  (stage/act! stage)
  (stage/draw! stage)
  (:stage/ctx stage))

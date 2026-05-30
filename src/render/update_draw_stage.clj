(ns render.update-draw-stage
  (:require [gdx.stage :as stage]))

(defn step
  [{:keys [ctx/stage] :as ctx}]
  (stage/set-ctx! stage ctx)
  (stage/act!  stage)
  (stage/draw! stage)
  (:stage/ctx stage))

(ns render.update-draw-stage
  (:require [gdx.scene2d.stage.draw :refer [draw!]]
            [gdx.scene2d.stage.set-ctx :refer [set-ctx!]]
            [gdx.scene2d.stage.act :refer [act!]]))

(defn step
  [{:keys [ctx/stage] :as ctx}]
  (set-ctx! stage ctx)
  (act! stage)
  (draw! stage)
  (:stage/ctx stage))

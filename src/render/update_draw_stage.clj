(ns render.update-draw-stage
  (:require [scene2d.stage.draw :refer [draw!]]
            [scene2d.stage.set-ctx :refer [set-ctx!]]
            [scene2d.stage.act :refer [act!]]))

(defn step
  [{:keys [ctx/stage] :as ctx}]
  (set-ctx! stage ctx)
  (act! stage)
  (draw! stage)
  (:stage/ctx stage))

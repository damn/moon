(ns render.update-draw-stage
  (:require [gdl.stage.draw :refer [draw!]]
            [gdl.stage.set-ctx :refer [set-ctx!]]
            [gdl.stage.act :refer [act!]]))

(defn step
  [{:keys [ctx/stage] :as ctx}]
  (set-ctx! stage ctx)
  (act! stage)
  (draw! stage)
  (:stage/ctx stage))

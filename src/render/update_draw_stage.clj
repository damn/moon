(ns render.update-draw-stage
  (:require [gdx.stage :as stage]
            [clojure.gdx.scene2d.stage.set-ctx :refer [set-ctx!]]))

(defn step
  [{:keys [ctx/stage] :as ctx}]
  (set-ctx! stage ctx)
  (stage/act!  stage)
  (stage/draw! stage)
  (:stage/ctx stage))

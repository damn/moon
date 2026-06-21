(ns render.update-draw-stage
  (:require [clojure.stage.draw :refer [draw!]]
            [clojure.stage.set-ctx :refer [set-ctx!]]
            [clojure.stage.act :refer [act!]]))

(defn step
  [{:keys [ctx/stage] :as ctx}]
  (set-ctx! stage ctx)
  (act! stage)
  (draw! stage)
  (:stage/ctx stage))

(ns clojure.update-draw-stage
  (:require [clojure.stage :as stage]
            [clojure.scene2d-stage :refer [set-ctx!]]))

(defn step
  [{:keys [ctx/stage] :as ctx}]
  (set-ctx! stage ctx)
  (stage/act! stage)
  (stage/draw! stage)
  (:stage/ctx stage))

(ns clojure.update-draw-stage
  (:require [clojure.stage :as stage]
            [clojure.set-ctx :as set-ctx]))

(defn step
  [{:keys [ctx/stage] :as ctx}]
  (set-ctx/f stage ctx)
  (stage/act! stage)
  (stage/draw! stage)
  (:stage/ctx stage))

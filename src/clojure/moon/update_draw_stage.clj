(ns clojure.moon.update-draw-stage
  (:require [clojure.set-ctx :as set-ctx]
            [gdl.stage :as stage]))

(defn f
  [{:keys [ctx/stage] :as ctx}]
  (set-ctx/f stage ctx)
  (stage/act! stage)
  (stage/draw! stage)
  (:stage/ctx stage))

(ns render.update-draw-stage
  (:require [clojure.gdx.stage.act :as act]
            [clojure.gdx.stage.draw :as draw]
            [clojure.gdx.stage.set-ctx :as set-ctx]))

(defn step
  [{:keys [ctx/stage] :as ctx}]
  (set-ctx/f stage ctx)
  (act/f stage)
  (draw/f stage)
  (:stage/ctx stage))

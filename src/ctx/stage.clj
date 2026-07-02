(ns ctx.stage
  (:require [scene2d.stage :as stage]
            [clojure.gdx.fit-viewport.new :as fit-viewport]
            [clojure.gdx.input.set-input-processor! :as set-input-processor!]))

(defn step
  [{:keys [ctx/input
           ctx/batch]}]
  (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
    (set-input-processor!/f input stage)
    stage))

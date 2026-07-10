(ns clojure.moon.create-stage
  (:require [gdl.utils.viewport.fit-viewport :as fit-viewport]
            [gdl.input :as input]
            [clojure.scene2d-stage :as scene2d-stage]))

(defn f [{:keys [ctx/input
                ctx/batch] :as ctx}]
  (let [stage* (scene2d-stage/create (fit-viewport/create 1440 900) batch)]
    (input/set-input-processor! input stage*)
    (assoc ctx :ctx/stage stage*)))

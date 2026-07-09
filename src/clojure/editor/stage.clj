(ns clojure.editor.stage
  (:require [clojure.editor.main-window :as main-window]
            [clojure.fit-viewport :as fit-viewport]
            [clojure.input :as input]
            [clojure.scene2d-stage :as scene2d-stage]
            [clojure.stage :as stage]))

(defn f [{:keys [ctx/input
                ctx/batch] :as ctx}]
  (let [stage* (scene2d-stage/create (fit-viewport/create 1440 900) batch)]
    (input/set-input-processor! input stage*)
    (let [ctx (assoc ctx :ctx/stage stage*)]
      (stage/add-actor! (:ctx/stage ctx) (main-window/f ctx))
      ctx)))

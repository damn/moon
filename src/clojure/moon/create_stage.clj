(ns clojure.moon.create-stage
  (:require [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.input :as input]
            [clojure.scene2d-stage :as scene2d-stage]))

(defn f [{:keys [ctx/input
                ctx/batch] :as ctx}]
  (let [stage* (scene2d-stage/create (fit-viewport/new 1440 900) batch)]
    (input/setInputProcessor input stage*)
    (assoc ctx :ctx/stage stage*)))

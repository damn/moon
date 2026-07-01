(ns ctx.stage
  (:require [scene2d.stage :as stage]
            [clojure.gdx.fit-viewport.new :as fit-viewport])
  (:import (com.badlogic.gdx Input)))

(defn step
  [{:keys [ctx/input
           ctx/batch]}]
  (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
    (.setInputProcessor ^Input input stage)
    stage))

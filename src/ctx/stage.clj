(ns ctx.stage
  (:require [scene2d.stage :as stage]
            [clojure.gdx :as gdx])
  (:import (com.badlogic.gdx Input)))

(defn step
  [{:keys [ctx/input
           ctx/batch]}]
  (let [stage (stage/create (gdx/fit-viewport 1440 900) batch)]
    (.setInputProcessor ^Input input stage)
    stage))

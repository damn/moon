(ns moon.create.set-input-processor
  (:import (com.badlogic.gdx Input)))

(defn step
  [{:keys [ctx/input
           ctx/stage]
    :as ctx}]
  (Input/.setInputProcessor input stage)
  ctx)

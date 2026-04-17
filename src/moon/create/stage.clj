(ns moon.create.stage
  (:require [clojure.gdx.stage :as stage]))

(defn step
  [{:keys [ctx/batch
           ctx/ui-viewport]
    :as ctx}]
  (assoc ctx :ctx/stage (stage/create ui-viewport batch)))

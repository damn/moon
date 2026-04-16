(ns moon.create.batch
  (:require [gdl.context :as context]))

(defn do! [ctx]
  (assoc ctx :ctx/batch (context/sprite-batch ctx)))

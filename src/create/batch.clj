(ns create.batch
  (:require [clojure.sprite-batch :as sprite-batch]))

(defn step [ctx]
  (assoc ctx :ctx/batch (sprite-batch/create)))

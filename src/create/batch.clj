(ns create.batch
  (:require [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]))

(defn step [ctx]
  (assoc ctx :ctx/batch (sprite-batch/create)))

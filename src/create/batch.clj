(ns create.batch
  (:require [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]))

(defn step [ctx]
  (assoc ctx :ctx/batch (sprite-batch/create)))

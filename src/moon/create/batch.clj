(ns moon.create.batch
  (:require [clj.api.com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]))

(defn do! [ctx]
  (assoc ctx :ctx/batch (sprite-batch/create)))

(ns create.batch
  (:require [gdx.sprite-batch :as sprite-batch]))

(defn step [_ctx]
  (sprite-batch/create))

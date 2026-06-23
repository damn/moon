(ns ctx.batch
  (:require [gdl.sprite-batch :as sprite-batch]))

(defn step [_ctx]
  (sprite-batch/create))

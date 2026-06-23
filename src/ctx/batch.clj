(ns ctx.batch
  (:require [gdx.graphics.g2d.sprite-batch :as sprite-batch]))

(defn step [_ctx]
  (sprite-batch/create))

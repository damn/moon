(ns create.batch
  (:require [clojure.graphics.g2d.sprite-batch :as sprite-batch]))

(defn step [_ctx]
  (sprite-batch/create))

(ns clojure.ctx-batch
  (:require [clojure.sprite-batch :as sprite-batch]))

(defn step [_ctx]
  (sprite-batch/new))

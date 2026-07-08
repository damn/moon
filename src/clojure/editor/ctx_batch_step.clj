(ns clojure.editor.ctx-batch-step
  (:require [clojure.ctx-batch :as ctx-batch]))

(defn f [ctx]
  (assoc ctx :ctx/batch (ctx-batch/step ctx)))

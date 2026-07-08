(ns clojure.moon.create-batch
  (:require [clojure.ctx-batch :as ctx-batch]))

(defn f [ctx]
  (assoc ctx :ctx/batch (ctx-batch/step ctx)))

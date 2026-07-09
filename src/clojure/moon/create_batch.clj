(ns clojure.moon.create-batch
  (:require [clojure.sprite-batch :as sprite-batch]))

(defn f [ctx]
  (assoc ctx :ctx/batch (sprite-batch/new)))

(ns clojure.moon.create-batch
  (:require [gdl.sprite-batch :as sprite-batch]))

(defn f [ctx]
  (assoc ctx :ctx/batch (sprite-batch/new)))

(ns moon.create.batch
  (:require [clojure.sprite-batch :as sprite-batch]))

(defn do! [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/batch (sprite-batch/create app)))

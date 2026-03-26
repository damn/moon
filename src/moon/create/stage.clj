(ns moon.create.stage
  (:require [moon.stage]
            [moon.viewport :as viewport])
  (:import (moon Stage)))

(defn step
  [{:keys [ctx/batch
           ctx/ui-viewport]
    :as ctx}]
  (assoc ctx :ctx/stage (Stage. ui-viewport batch)))

(extend-type Stage
  moon.stage/Stage
  (mouseover-actor [stage position]
    (let [[x y] (viewport/unproject (.getViewport stage) position)]
      (.hit stage x y true)))
  )

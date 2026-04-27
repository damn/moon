(ns moon.create.shape-drawer
  (:require [clojure.graphics.shape-drawer :as shape-drawer]
            [clojure.graphics.texture :as texture]))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]
    :as ctx}]
  (assoc ctx :ctx/shape-drawer (shape-drawer/create batch (texture/region shape-drawer-texture 1 0 1 1))))

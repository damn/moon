(ns clojure.ctx.shape-drawer
  (:require [gdl.texture.region :as region]
            [gdl.shape-drawer :refer [shape-drawer]]))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (shape-drawer batch (region/f shape-drawer-texture 1 0 1 1)))

(ns ctx.shape-drawer
  (:require [clojure.texture-region :as texture-region]
            [gdx.graphics.shape-drawer :as shape-drawer]
            [clojure.texture :as texture]))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (shape-drawer/new batch (texture-region/new shape-drawer-texture 1 0 1 1)))

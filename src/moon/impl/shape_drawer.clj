(ns moon.impl.shape-drawer
  (:require [clojure.gdx.shape-drawer :as shape-drawer]
            [clojure.graphics.texture :as texture]))

(defn create
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (shape-drawer/create batch (texture/region shape-drawer-texture 1 0 1 1)))

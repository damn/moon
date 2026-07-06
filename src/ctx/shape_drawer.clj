(ns ctx.shape-drawer
  (:require
            [space.earlygrey.shapedrawer.shape-drawer :as shape-drawer]
            [com.badlogic.gdx.graphics.texture :as texture]
            [clojure.gdx.texture-region.new :as texture-region]))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (shape-drawer/new batch (texture-region/f shape-drawer-texture 1 0 1 1)))

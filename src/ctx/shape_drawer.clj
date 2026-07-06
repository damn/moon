(ns ctx.shape-drawer
  (:require
            [com.badlogic.gdx.graphics.texture :as texture] [clojure.gdx.shape-drawer.new :as new-shape-drawer]
            [clojure.gdx.texture-region.new :as texture-region]))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (new-shape-drawer/f batch (texture-region/f shape-drawer-texture 1 0 1 1)))

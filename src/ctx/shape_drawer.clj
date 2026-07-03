(ns ctx.shape-drawer
  (:require [clojure.gdx.texture-region.new :as texture-region])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (ShapeDrawer. batch (texture-region/f shape-drawer-texture 1 0 1 1)))

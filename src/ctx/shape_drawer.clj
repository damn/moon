(ns ctx.shape-drawer
  (:require [texture.texture-region :as texture-region])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (ShapeDrawer. batch (texture-region/f shape-drawer-texture 1 0 1 1)))

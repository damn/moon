(ns ctx.shape-drawer
  (:require [clojure.gdx :as gdx])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (ShapeDrawer. batch (gdx/texture-region shape-drawer-texture 1 0 1 1)))

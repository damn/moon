(ns ctx.shape-drawer
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (ShapeDrawer. batch (TextureRegion. shape-drawer-texture 1 0 1 1)))

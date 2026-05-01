(ns moon.application.create.shape-drawer
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]
    :as ctx}]
  (assoc ctx :ctx/shape-drawer (ShapeDrawer. batch (TextureRegion. shape-drawer-texture 1 0 1 1))))

(ns draw.rectangle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f
  [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
  (ShapeDrawer/.setColor shape-drawer (float color-float-bits))
  (ShapeDrawer/.rectangle shape-drawer x y w h))

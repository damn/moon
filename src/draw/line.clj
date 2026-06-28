(ns draw.line
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f
  [{:keys [ctx/shape-drawer]} [sx sy] [ex ey] color-float-bits]
  (ShapeDrawer/.setColor shape-drawer (float color-float-bits))
  (ShapeDrawer/.line shape-drawer (float sx) (float sy) (float ex) (float ey)))
